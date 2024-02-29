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
    String INPUT_NAME = "parentBirth";
    String EARLIEST_DATE_SUPPORTED = "01/01/1901";
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> inputData = formSubmission.getFormData();
    DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
    String parentDate = String.format("%s/%s/%s",
        (String) inputData.get(INPUT_NAME + "Month"),
        (String) inputData.get(INPUT_NAME + "Day"),
        (String) inputData.get(INPUT_NAME + "Year"));

    if (this.isDateInvalid(parentDate)) {
      errorMessages.put(INPUT_NAME + "Date", List.of(messageSource.getMessage("errors.invalid-date-entered", null, locale)));
      return errorMessages;
    }

    if (this.isDateNotWithinSupportedRange(dtf.parseDateTime(parentDate), dtf.parseDateTime(EARLIEST_DATE_SUPPORTED), null)) {
      errorMessages.put(INPUT_NAME + "Date", List.of(
          (messageSource.getMessage("errors.date-outside-of-supported-range", List.of(EARLIEST_DATE_SUPPORTED).toArray(),
              locale))));
      return errorMessages;
    }

    return errorMessages;
  }
}
