package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.DateUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateEmployerStartDate extends VerifyDate {
  
  @Autowired
  MessageSource messageSource;
  private final String APPLICANT_JOB_START = "activitiesJobStart";
  
  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
    log.info("Running ValidateJobStartDate");
    
    Locale locale = LocaleContextHolder.getLocale();
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> inputData = formSubmission.getFormData();
    inputData.getOrDefault(APPLICANT_JOB_START, new ArrayList<>());
    String employerStartDate = DateUtilities.getFormattedDateFromMonthDateYearInputs("activitiesJobStart", inputData);

    if (employerStartDate.replace("/", "").isBlank()) {
      return errorMessages;
    }
    
    if (this.isDateInvalid(employerStartDate)) {
        errorMessages.put(APPLICANT_JOB_START, List.of(messageSource.getMessage("activities-employer-start-date.error", null, locale)));
    } else if (this.isBeforeMinDate(employerStartDate)) {
        errorMessages.put(APPLICANT_JOB_START, List.of(messageSource.getMessage("activities-employer-start-date.error", null, locale)));
    }
    
    return errorMessages;
  }
}
