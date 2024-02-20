package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateChildrenCCAPStartDate extends VerifyDate {
@Autowired
  MessageSource messageSource;

  private final String INPUT_NAME = "ccapStartDate";
  private static final String EARLIEST_DATE_SUPPORTED = "01/01/1901";
  private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
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
      errorMessages.put(INPUT_NAME, List.of(messageSource.getMessage("errors.invalid-date-entered", null, locale)));
    }else{
      DateTime dateCCAPStart = dtf.parseDateTime(ccapStartingDate);
      DateTime present = DateTime.now();
      DateTime earliest_supported_date = dtf.parseDateTime(EARLIEST_DATE_SUPPORTED);

      if(this.isDateNotWithinSupportedRange(dateCCAPStart, earliest_supported_date, present)){
        errorMessages.put(INPUT_NAME, List.of((messageSource.getMessage("errors.date-outside-of-supported-range", List.of(ccapStartingDate).toArray(), locale))));
      }
    }


    return errorMessages;
  }
}
