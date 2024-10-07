package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Collections.emptyList;

@Slf4j
@Component
public class ValidateChildrenCCAPStartDate extends VerifyDate {
  
  @Autowired
  MessageSource messageSource;
  private final String INPUT_NAME = "ccapStartDate";
  
  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
    log.info("Running ValidateCCAPStartDate");
    
    Locale locale = LocaleContextHolder.getLocale();
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> inputData = formSubmission.getFormData();
    
    String ccapStartingDate = String.format("%s/%s/%s",
        (String) inputData.get("ccapStartMonth"),
        (String) inputData.get("ccapStartDay"),
        (String) inputData.get("ccapStartYear"));
    
    if (this.isDateInvalid(ccapStartingDate)) {
        errorMessages.put(INPUT_NAME, List.of(messageSource.getMessage("errors.invalid-date-format", null, locale)));
    } else if (this.isBeforeMinDate(ccapStartingDate)) {
        errorMessages.put(INPUT_NAME, List.of(messageSource.getMessage("errors.invalid-min-date", null, locale)));
    }
    
    return errorMessages;
  }
}
