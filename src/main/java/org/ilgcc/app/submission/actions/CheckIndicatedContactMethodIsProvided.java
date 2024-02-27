package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckIndicatedContactMethodIsProvided implements Action {
  @Autowired
  MessageSource messageSource;

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
    log.info("Running CheckIndicatedContactMethodIsProvided");
    String HOW_TO_CONTACT_YOU_INPUT = "howToContactYou[]";
    String PHONE_NUMBER_INPUT = "phoneNumber";
    String EMAIL_INPUT = "email";
    
    Map<String, List<String>> errorMessages = new java.util.HashMap<>(Collections.emptyMap());

    if (formSubmission.getFormData().containsKey(HOW_TO_CONTACT_YOU_INPUT)) {
      ArrayList<String> preferredContactMethods = (ArrayList<String>) formSubmission.getFormData().get(HOW_TO_CONTACT_YOU_INPUT);
      if (preferredContactMethods.contains("phone") && formSubmission.getFormData().get(PHONE_NUMBER_INPUT).equals("")) {
        errorMessages.put(PHONE_NUMBER_INPUT, 
            List.of(messageSource.getMessage("contact-info.error-phone-selected", null, null)));
      }  
      if (preferredContactMethods.contains("email") && formSubmission.getFormData().get("email").equals("")) {
        errorMessages.put(EMAIL_INPUT,
            List.of(messageSource.getMessage("contact-info.error-email-selected", null, null)));
      }
    }
    return errorMessages;
  }
}
