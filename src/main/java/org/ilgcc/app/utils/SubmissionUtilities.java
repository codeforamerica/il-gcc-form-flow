package org.ilgcc.app.utils;

import formflow.library.data.Submission;
import formflow.library.inputs.FieldNameMarkers;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import org.ilgcc.app.utils.ActivitySchedules.ConsistentHourlySchedule;
import org.ilgcc.app.utils.ActivitySchedules.HourlySchedule;
import org.ilgcc.app.utils.ActivitySchedules.LocalTimeRange;
import org.ilgcc.app.utils.ActivitySchedules.PerDayHourlySchedule;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;


public class SubmissionUtilities {
  public static final DateTimeFormatter MM_DD_YYYY = DateTimeFormatter.ofPattern("M/d/uuuu");
  public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyy");
  public static final String PROGRAM_SCHEDULE = "programSchedule";

  /**
   * Formats the date portion of {@code submittedAt} to look like "February 7, 2023".
   *
   * @param submission the submission from which the {@code submittedAt} is to be formatted
   * @return the string formatted date
   */
  public static String getFormattedSubmittedAtDate(Submission submission) {
    return dateTimeFormatter.format(submission.getSubmittedAt());
  }

  /**
   * @param submission submission containing input data to use
   * @return the string Yes/No
   */
  public static String getProgramSchedule(Submission submission) {
    return submission.getInputData().get(PROGRAM_SCHEDULE).toString();
  }

  /**
   * @param inputData a JSON object of user inputs
   * @return true or false
   */
  public static boolean parentIsExperiencingHomelessness(Map<String, Object> inputData) {
    return inputData.getOrDefault("parentHomeExperiencingHomelessness[]", "no").equals(List.of("yes"));
  }

  /**
   * @param inputData a JSON object of user inputs
   * @return true or false
   */
  public static boolean parentMailingAddressIsHomeAddress(Map<String, Object> inputData) {
    return inputData.getOrDefault("parentMailingAddressSameAsHomeAddress[]", "no").equals(List.of("yes"));
  }

  public static boolean hasAddressSuggestion(Submission submission) {
    return submission.getInputData().get(FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATE_ADDRESS + "parentMailing")
        .equals("true") && submission.getInputData()
        .containsKey("parentMailingStreetAddress1" + FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATED);
  }

  /**
   * Mixpanel helper method
   */
  public static String getMixpanelValue(Map<String, Object> inputData, String inputName) {
    String value = inputData == null ? "not_set" : (String) inputData.getOrDefault(inputName, "not_set");

    if (inputName.equals("parentHasPartner"))
      if (!"not_set".equals(value)) {
        value = "true".equals(value) ? "dual_parent" : "single_parent";
      }
    return value;
  }

  public static String getMixpanelValue(Submission submission, String subflow) {
    if (submission == null || !submission.getInputData().containsKey(subflow)) {
      return "not_set";
    }

    return String.valueOf(((List<Map<String, Object>>) submission.getInputData().get(subflow)).stream()
        .filter(iter -> (boolean) iter.getOrDefault("iterationIsComplete", false))
        .toList().size());
  }

  public static Optional<LocalDate> getDateInput(Submission submission, String inputName) {
    String year = (String) submission.getInputData().get("%sYear".formatted(inputName));
    String month = (String) submission.getInputData().get("%sMonth".formatted(inputName));
    String day = (String) submission.getInputData().get("%sDay".formatted(inputName));
    if (year == null && month == null && day == null) {
      return Optional.empty();
    } else if (year == null || month == null || day == null) {
      throw new IllegalArgumentException("Date must be complete if specified");
    }
    return Optional.of(LocalDate.of(parseInt(year), parseInt(month), parseInt(day)));
  }

  public static Optional<LocalTime> getTimeInput(Map<String, Object> inputData, String inputName) {
    String rawValue = (String) inputData.getOrDefault(inputName, "");
    if (rawValue.isBlank()) {
      return Optional.empty();
    } else {
      LocalTime time = LocalTime.parse((String) inputData.get(inputName));
      return Optional.of(time);
    }
  }

  public static Optional<LocalTimeRange> getTimeRangeInput(Map<String, Object> inputData, String inputName, String suffix) {
    Optional<LocalTime> start = getTimeInput(inputData, "%sStartTime%s".formatted(inputName, suffix));
    Optional<LocalTime> end = getTimeInput(inputData, "%sEndTime%s".formatted(inputName, suffix));
     if (start.isEmpty() && end.isEmpty()) {
       return Optional.empty();
     } else {
      return Optional.of(new LocalTimeRange(start.orElseThrow(), end.orElseThrow()));
     }
  }

  public static String formatToStringFromLocalDate(Optional<LocalDate> date){
    if(date.isPresent()){
      return date.get().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }
    return "";
  }


  public static String selectedYes(String selected){
    if (selected.equals("Yes")){
      return "true";
    } else {
      return "false";
    }
  }

  public static List<Map<String, Object>> getChildren(Submission submission) {
    return (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
  }

  public static List<Map<String, Object>> getChildrenNeedingAssistance(Submission submission) {
    return getChildren(submission).stream().filter(
                    child -> child.getOrDefault("needFinancialAssistanceForChild", "No").equals("Yes"))
            .toList();
  }

  public static Optional<List<DayOfWeekOption>> getDaysOfWeekField(Map<String, Object> inputData, String inputName) {
    Object value = inputData.get(inputName);
    if (value == null) {
      return Optional.empty();
    } else if (value instanceof List<?> valueList) {
      return Optional.of(valueList.stream().map(d -> DayOfWeekOption.valueOf(d.toString())).toList());
    } else {
      throw new IllegalArgumentException("Weekdays field does not contain a list");
    }
  }

  public static Optional<HourlySchedule> getHourlySchedule(
          Submission submission, String inputName, String weeklyScheduleInputName) {
    return getHourlySchedule(submission.getInputData(), inputName, weeklyScheduleInputName);
  }

  public static Optional<HourlySchedule> getHourlySchedule(
          Map<String, Object> inputData, String inputName, String weeklyScheduleInputName) {
    Optional<List<DayOfWeekOption>> daysActive = getDaysOfWeekField(inputData, weeklyScheduleInputName);
    if (daysActive.isEmpty()) {
      return Optional.empty();
    }

    List<String> sameEveryDayField = getOptionalListField(
            inputData, "%sHoursSameEveryDay[]".formatted(inputName), Object::toString).orElse(List.of());
    boolean sameEveryDay = !sameEveryDayField.isEmpty() && sameEveryDayField.get(0).equalsIgnoreCase("Yes");

    if (sameEveryDay) {
      LocalTimeRange scheduleEveryDay = getTimeRangeInput(inputData, inputName, "AllDays").orElseThrow();
      return Optional.of(new ConsistentHourlySchedule(daysActive.get(), scheduleEveryDay));
    } else {
      var dailyScheduleMap = daysActive.get().stream().collect(
              toImmutableMap(
                      identity(),
                      day -> getTimeRangeInput(inputData, inputName, day.name()).orElseThrow()));
      return Optional.of(new PerDayHourlySchedule(dailyScheduleMap));
    }
  }

  public static <T> Optional<List<T>> getOptionalListField(
          Map<String, Object> inputData, String fieldName, Function<Object, T> converter) {
    Object value = inputData.get(fieldName);
    if (value== null) {
      return Optional.empty();
    } else if (value instanceof List<?> valueList) {
      return Optional.of(valueList.stream().map(converter).toList());
    } else {
      throw new IllegalArgumentException("List field does not contain a list");
    }
  }

  public static void putSingleFieldResult(Map<String, SubmissionField> results, String fieldName, String value) {
    putSingleFieldResult(results, fieldName, value, null);
  }
  public static void putSingleFieldResult(Map<String, SubmissionField> results, String fieldName, String value, Integer iteration) {
    results.put(fieldName, new SingleField(fieldName, value, iteration));
  }
}
