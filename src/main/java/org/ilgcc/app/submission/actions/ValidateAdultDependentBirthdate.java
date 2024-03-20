package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ValidateAdultDependentBirthdate extends ValidateBirthdate {
  public static final String INPUT_NAME = "adultDependentBirthdate";
  public ValidateAdultDependentBirthdate(MessageSource messageSource) {
    super(messageSource, INPUT_NAME, INPUT_NAME);
  }

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
    if (isBlank(formSubmission)) return Collections.emptyMap();

    return super.runValidation(formSubmission, submission);
  }

  private static boolean isBlank(FormSubmission formSubmission) {
    Map<String, Object> inputData = formSubmission.getFormData();
    var month = (String) inputData.getOrDefault(INPUT_NAME + "Month", "");
    var day = (String) inputData.getOrDefault(INPUT_NAME + "Day", "");
    var year = (String) inputData.getOrDefault(INPUT_NAME + "Year", "");
    return month.isBlank() && day.isBlank() && year.isBlank();
  }
}
