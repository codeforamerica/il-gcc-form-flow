package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ValidateBirthdate extends VerifyDate {
  private final MessageSource messageSource;

  public ValidateBirthdate(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
    Map<String, Object> inputData = formSubmission.getFormData();
    String month = null, day = null, year = null;
    String dateInputName = null;
    Locale locale = LocaleContextHolder.getLocale();
    for (String inputName : inputData.keySet()) {
      if (inputName.endsWith("Month")) {
        month = (String) inputData.get(inputName);
        dateInputName = inputName.substring(0, inputName.length() - 5);
      } else if (inputName.endsWith("Day")) {
        day = (String) inputData.get(inputName);
      } else if (inputName.endsWith("Year")) {
        year = (String) inputData.get(inputName);
      }
    }

    if (month == null || day == null || year == null) {
      throw new IllegalStateException("Missing date fields to run validation");
    }

    if (month.isBlank() || day.isBlank() || year.isBlank()) {
      return Map.of(dateInputName, List.of(messageSource.getMessage("errors.provide-birthday", null, locale)));
    }

    String birthdate = String.format("%s/%s/%s", month, day, year);
    if (isDateInvalid(birthdate)) {
      return Map.of(dateInputName, List.of(messageSource.getMessage("errors.invalid-birthdate-format", null, locale)));
    }

    if (!isBetweenNowAndMinDate(birthdate)) {
      return Map.of(dateInputName, List.of(messageSource.getMessage("errors.invalid-birthdate-range", null, locale)));
    }

    return Collections.emptyMap();
  }
}
