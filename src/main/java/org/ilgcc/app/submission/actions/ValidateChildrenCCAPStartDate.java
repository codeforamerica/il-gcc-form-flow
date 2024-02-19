package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateChildrenCCAPStartDate extends VerifyDate {

  private final String INPUT_NAME = "ccapStartDate";
  private final String EARLIEST_DATE_SUPPORTED = "01/01/1901";

  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
    log.info("Running ValidateCCAPStartDate");
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> inputData = formSubmission.getFormData();
    String ccapStartingDate = String.format("%s/%s/%s",
                    (String) inputData.get("ccapStartMonth"),
                    (String) inputData.get("ccapStartDay"),
                    (String) inputData.get("ccapStartYear"));

    if (this.isDateInvalid(ccapStartingDate)) {
      errorMessages.put(INPUT_NAME, List.of("Please check the date entered. It is not a valid date."));
    }else{
      DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
      DateTime dateCCAPStart = dtf.parseDateTime(ccapStartingDate);
      DateTime present = DateTime.now();
      DateTime earliest_supported_date = dtf.parseDateTime(EARLIEST_DATE_SUPPORTED);

      if(this.isDateNotWithinSupportedRange(dateCCAPStart, earliest_supported_date, present)){
        errorMessages.put(INPUT_NAME, List.of(String.format("Please check the date entered. %s is not a supported start date.", ccapStartingDate)));
      }
    }


    return errorMessages;
  }
}
