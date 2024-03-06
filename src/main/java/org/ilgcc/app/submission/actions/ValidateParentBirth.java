package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import java.util.HashMap;
import java.util.List;
import formflow.library.data.Submission;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateParentBirth extends VerifyDate {

  @Autowired
  MessageSource messageSource;

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
    log.info(String.format("Running %s", this.getClass().getName()));
    Locale locale = LocaleContextHolder.getLocale();
    String inputNamePrefix = "parentBirth";
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> inputData = formSubmission.getFormData();
    String parentDate = String.format("%s/%s/%s",
        (String) inputData.get(inputNamePrefix + "Month"),
        (String) inputData.get(inputNamePrefix + "Day"),
        (String) inputData.get(inputNamePrefix + "Year"));

    if (isDateInvalid(parentDate)) {
      errorMessages.put(inputNamePrefix + "Date", List.of(messageSource.getMessage("errors.provide-birthday", null, locale)));
      return errorMessages;
    }

    if (!isBetweenNowAndMinDate(parentDate)) {
      return Map.of(inputNamePrefix + "Date", List.of(messageSource.getMessage("errors.invalid-birthdate-range", null, locale)));
    }

    return errorMessages;
  }
}
