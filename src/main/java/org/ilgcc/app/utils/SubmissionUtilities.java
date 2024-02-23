package org.ilgcc.app.utils;

import formflow.library.data.Submission;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class SubmissionUtilities {

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

  public static List<Map<String, Object>> getCompleteIterations(Submission submission, String subflow) {
    var iterations = (List<Map<String, Object>>) submission.getInputData().getOrDefault(subflow, emptyList());
    return iterations.stream()
        .filter(iter -> (boolean) iter.getOrDefault("iterationIsComplete", false))
        .toList();
  }
}
