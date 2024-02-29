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
    if (isDateEnteredBlank(inputData)){
      return errorMessages;
    }
    String ccapStartingDate = String.format("%s/%s/%s",
        (String) inputData.get("ccapStartMonth"),
        (String) inputData.get("ccapStartDay"),
        (String) inputData.get("ccapStartYear"));
    String current_uuid = (String) inputData.get("current_uuid");
    DateTime present = DateTime.now();

    boolean inChildCare = isChildInChildcare(submission, current_uuid);
    if (this.isDateInvalid(ccapStartingDate)) {
      if(inChildCare){
        errorMessages.put(INPUT_NAME, List.of(messageSource.getMessage("errors.invalid-past-date-entered", null, locale)));
      }else {
        errorMessages.put(INPUT_NAME, List.of(messageSource.getMessage("errors.invalid-future-date-entered", null, locale)));
      }
    } else if (inChildCare) {
      if (!isBetweenNowAndMinDate(ccapStartingDate)) {
        errorMessages.put(INPUT_NAME, List.of(
            (messageSource.getMessage("errors.past-childcare-date-out-of-range", List.of(ccapStartingDate).toArray(), locale))));
      }
    }else{
      DateTime dateCCAPStart = DTF.parseDateTime(ccapStartingDate);
      if(this.isDateNotWithinSupportedRange(dateCCAPStart, present, null)){
        errorMessages.put(INPUT_NAME, List.of((messageSource.getMessage("errors.future-childcare-date-outside-of-range", List.of(ccapStartingDate).toArray(), locale))));
      }
    }
    return errorMessages;
  }

  public Boolean isChildInChildcare(Submission submission, String uuid){
    var children = (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
    for(var child : children) {
      if(child.get("uuid").equals(uuid)) {
        return child.getOrDefault("childInCare", "false").equals("true");
      }
    }
    return false;
  }
  public Boolean isDateEnteredBlank(Map<String, Object> inputData){
    boolean blankCCAPMonth = inputData.get("ccapStartMonth").toString().isBlank();
    boolean blankCCAPDay  = inputData.get("ccapStartMonth").toString().isBlank();
    boolean blankCCAPYear  = inputData.get("ccapStartMonth").toString().isBlank();

    return blankCCAPMonth && blankCCAPDay && blankCCAPYear;
  }
}
