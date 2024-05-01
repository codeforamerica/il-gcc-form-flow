package org.ilgcc.app.utils;

import formflow.library.data.Submission;
import formflow.library.inputs.FieldNameMarkers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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


  public static String selectedYes(String selected){
    if (selected.equals("Yes")){
      return "true";
    } else {
      return "false";
    }
  }
}
