package org.ilgcc.app.submission.actions;


import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class ValidateProgramDates extends VerifyDate {

  final MessageSource messageSource;

  private final String INPUT_NAME_START = "partnerProgramStart";
  private final String INPUT_NAME_END = "partnerProgramEnd";

  public ValidateProgramDates(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
    Map<String, Object> inputData = formSubmission.getFormData();

    var startDay = (String) inputData.get(INPUT_NAME_START + "Day");
    var startMonth = (String) inputData.get(INPUT_NAME_START + "Month");
    var startYear = (String) inputData.get(INPUT_NAME_START + "Year");
    Map<String, List<String>> errorMessages = validateProgramDates(INPUT_NAME_START, startMonth, startDay, startYear);

    var endDay = (String) inputData.get(INPUT_NAME_END + "Day");
    var endMonth = (String) inputData.get(INPUT_NAME_END + "Month");
    var endYear = (String) inputData.get(INPUT_NAME_END + "Year");
    errorMessages.putAll(validateProgramDates(INPUT_NAME_END, endMonth, endDay, endYear));

    return errorMessages;
  }

  private Map<String, List<String>> validateProgramDates(String prefix, String month, String day, String year) {
    Map<String, List<String>> errorMessages = new HashMap<>();
    var dateString = String.format("%s/%s/%s", month, day, year);
    if (!dateString.equals("//")) {
      // Day field is not required, but we want to check for valid date
      if (day.isBlank()) {
        dateString = String.format("%s/01/%s", month, year);
      }

      Locale locale = LocaleContextHolder.getLocale();
      if (month.isBlank()) {
        errorMessages.put(prefix + "Month", List.of(messageSource.getMessage("general.month.validation", null, locale)));
      }

      if (year.isBlank()) {
        errorMessages.put(prefix + "Year", List.of(messageSource.getMessage("general.year.validation", null, locale)));
      }

      if (errorMessages.isEmpty() && isDateInvalid(dateString)) {
        errorMessages.put(prefix, List.of(messageSource.getMessage("errors.invalid-date-format", null, locale)));
      }
    }
    return errorMessages;
  }
}
