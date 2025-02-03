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
  private final String PARTNER_JOB_START = "activitiesPartnerJobStart";
  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
    log.info("Running ValidateEmployerStartDate");

    String inputName = formSubmission.getFormData().containsKey(APPLICANT_JOB_START + "Year") ? APPLICANT_JOB_START : PARTNER_JOB_START;
    Locale locale = LocaleContextHolder.getLocale();
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> inputData = formSubmission.getFormData();
    inputData.getOrDefault(inputName, new ArrayList<>());
    String employerStartDate = DateUtilities.getFormattedDateFromMonthDateYearInputs(inputName, inputData);

    if (employerStartDate.replace("/", "").isBlank()) {
      return errorMessages;
    }

    //Changes that day in the employerStartDate string to 1 in the event that no day was entered.
    if (inputData.get(inputName + "Day").toString().isEmpty()){
      employerStartDate = DateUtilities.formatDateStringFromMonthDayYear((String) inputData.get(inputName+"Month"), "1", (String) inputData.get(inputName + "Year"));
    }


    if (this.isDateInvalid(employerStartDate)) {
        errorMessages.put(inputName, List.of(messageSource.getMessage("activities-employer-start-date.error", null, locale)));
    } else if (this.isBeforeMinDate(employerStartDate)) {
        errorMessages.put(inputName, List.of(messageSource.getMessage("activities-employer-start-date.error", null, locale)));
    }
    
    return errorMessages;
  }
}
