package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class ValidateBirthdate extends VerifyDate {
  MessageSource messageSource;
  private final String inputName;
  private final String groupName;

  public ValidateBirthdate(MessageSource messageSource, String inputName, String groupName) {
    this.messageSource = messageSource;
    this.inputName = inputName;
    this.groupName = groupName;
  }

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
    Map<String, Object> inputData = formSubmission.getFormData();
    String month = (String) inputData.get(inputName + "Month");
    String day = (String) inputData.get(inputName + "Day");
    String year = (String) inputData.get(inputName + "Year");

    Locale locale = LocaleContextHolder.getLocale();
    if (month == null || day == null || year == null) {
      log.warn("All Fields Must Be Present. One or more fields are null.  Month: {}, Day: {}, Year: {}", month, day, year);
      return Map.of(groupName, List.of(messageSource.getMessage("errors.provide-birthday", null, locale)));
    }

    if (month.isBlank() && day.isBlank() && year.isBlank()) {
      return Map.of(groupName, List.of(messageSource.getMessage("errors.provide-birthday", null, locale)));
    }

    String birthdate = String.format("%s/%s/%s", month, day, year);
    if (isDateInvalid(birthdate)) {
      return Map.of(groupName, List.of(messageSource.getMessage("errors.invalid-birthdate-format", null, locale)));
    }

    if (isNotBetweenNowAndMinDate(birthdate)) {
      return Map.of(groupName, List.of(messageSource.getMessage("errors.invalid-date-range", null, locale)));
    }

    return Collections.emptyMap();
  }
}
