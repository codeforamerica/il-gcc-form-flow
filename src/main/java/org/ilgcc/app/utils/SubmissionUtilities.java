package org.ilgcc.app.utils;

import com.google.common.collect.ImmutableMap;
import formflow.library.data.Submission;
import formflow.library.inputs.FieldNameMarkers;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

import static java.lang.Integer.parseInt;


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

  public static Optional<LocalTime> getTimeInput(Submission submission, String inputName) {
    String rawValue = (String) submission.getInputData().getOrDefault(inputName, "");
    if (rawValue.isBlank()) {
      return Optional.empty();
    } else {
      LocalTime time = LocalTime.parse((String) submission.getInputData().get(inputName));
      return Optional.of(time);
    }
  }

  public static Optional<LocalTimeRange> getTimeRangeInput(Submission submission, String inputName, String suffix) {
    Optional<LocalTime> start = getTimeInput(submission, "%sStartTime%s".formatted(inputName, suffix));
    Optional<LocalTime> end = getTimeInput(submission, "%sEndTime%s".formatted(inputName, suffix));
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

  public record LocalTimeRange(LocalTime startTime, LocalTime endTime) { }

  public interface HourlySchedule {
    ImmutableMap<DayOfWeekOption, LocalTimeRange> toDayMap();
  }

  public record ConsistentHourlySchedule(List<DayOfWeekOption> weekdays, LocalTimeRange allDays) implements HourlySchedule {
    @Override
    public ImmutableMap<DayOfWeekOption, LocalTimeRange> toDayMap() {
      ImmutableMap.Builder<DayOfWeekOption, LocalTimeRange> map = ImmutableMap.builder();
      for (DayOfWeekOption day : weekdays) {
        map.put(day, allDays);
      }
      return map.build();
    }
  }

  public record PerDayHourlySchedule(
          ImmutableMap<DayOfWeekOption, LocalTimeRange> dayMap
  ) implements HourlySchedule {
    public static PerDayHourlySchedule fromEntries(Iterable<ImmutableMap.Entry<DayOfWeekOption, LocalTimeRange>> dayEntries) {
      ImmutableMap.Builder<DayOfWeekOption, LocalTimeRange> map = ImmutableMap.builder();
      for (var entry : dayEntries) {
        map.put(entry.getKey(), entry.getValue());
      }
      return new PerDayHourlySchedule(map.build());
    }
    @Override
    public ImmutableMap<DayOfWeekOption, LocalTimeRange> toDayMap() {
      return dayMap;
    }
  }

  public static Optional<List<DayOfWeekOption>> getDaysOfWeekField(Submission submission, String inputName) {
    Object value = submission.getInputData().get(inputName);
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
    Optional<List<DayOfWeekOption>> days = getDaysOfWeekField(submission, weeklyScheduleInputName);
    if (days.isEmpty()) {
      return Optional.empty();
    }

    List<String> sameEveryDayField = getOptionalListField(
            submission, "%sHoursSameEveryDay[]".formatted(inputName), Object::toString).orElse(List.of());
    boolean sameEveryDay = !sameEveryDayField.isEmpty() && sameEveryDayField.get(0).equalsIgnoreCase("Yes");

    if (sameEveryDay) {
      LocalTimeRange allDays = getTimeRangeInput(submission, inputName, "AllDays").orElseThrow();
      return Optional.of(new ConsistentHourlySchedule(days.get(), allDays));
    } else {
      List<ImmutableMap.Entry<DayOfWeekOption, LocalTimeRange>> ranges = new ArrayList<>();
      for (var day : days.get()) {
        LocalTimeRange range = getTimeRangeInput(submission, inputName, day.name()).orElseThrow();
        ranges.add(Map.entry(day, range));
      }
      return Optional.of(PerDayHourlySchedule.fromEntries(ranges));
    }
  }

  public static <T> Optional<List<T>> getOptionalListField(
          Submission submission, String fieldName, Function<Object, T> converter) {
    Object value = submission.getInputData().get(fieldName);
    if (value== null) {
      return Optional.empty();
    } else if (value instanceof List<?> valueList) {
      return Optional.of(valueList.stream().map(converter).toList());
    } else {
      throw new IllegalArgumentException("List field does not contain a list");
    }
  }

  public static void putSingleFieldResult(Map<String, SubmissionField> results, String fieldName, String value) {
    results.put(fieldName, new SingleField(fieldName, value, null));
  }
}
