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
public class ValidatePartnerEmployerStartDate extends VerifyDate {
  
  @Autowired
  MessageSource messageSource;
  private final String PARTNER_EMPLOYER_START_DATE = "activitiesPartnerJobStart";

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
    log.info("Running ValidatePartnerEmployerStartDate");
    
    Locale locale = LocaleContextHolder.getLocale();
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> inputData = formSubmission.getFormData();
    inputData.getOrDefault(PARTNER_EMPLOYER_START_DATE, new ArrayList<>());
    String employerStartDate = DateUtilities.getFormattedDateFromMonthDateYearInputs("activitiesPartnerJobStart", inputData);

    if (employerStartDate.replace("/", "").isBlank()) {
      return errorMessages;
    }
    
    if (this.isDateInvalid(employerStartDate)) {
        errorMessages.put(PARTNER_EMPLOYER_START_DATE, List.of(messageSource.getMessage("activities-employer-start-date.error", null, locale)));
    } else if (this.isBeforeMinDate(employerStartDate)) {
        errorMessages.put(PARTNER_EMPLOYER_START_DATE, List.of(messageSource.getMessage("activities-employer-start-date.error", null, locale)));
    }
    
    return errorMessages;
  }
}
