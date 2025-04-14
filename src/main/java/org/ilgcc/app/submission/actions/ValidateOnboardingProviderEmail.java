package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendGridEmailValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateOnboardingProviderEmail implements Action {
  @Autowired
  MessageSource messageSource;

  @Autowired
  SendGridEmailValidationService sendGridEmailValidationService;
  private final String INPUT_NAME = "familyIntendedProviderEmail";

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

    Locale locale = LocaleContextHolder.getLocale();
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> formData = formSubmission.getFormData();
    String providerEmail = formData.get(INPUT_NAME).toString();
    if (providerEmail.isBlank()) {
      return errorMessages;
    }
    try {
      HashMap<String, String> emailValidationResult = sendGridEmailValidationService.validateEmail(providerEmail);
      if (emailValidationResult.get("endpointReached").equals("success")) {
        if (emailValidationResult.get("emailIsValid").equals("true")){
          return errorMessages;
        }else{
          if (emailValidationResult.get("hasSuggestion").equals("true")){
            errorMessages.put(INPUT_NAME, List.of(messageSource.getMessage("errors.invalid-email.with-suggested-email-address", new Object[]{emailValidationResult.get("suggestedEmail")}, locale)));
          }else{
            errorMessages.put(INPUT_NAME, List.of(messageSource.getMessage("errors.invalid-email.no-suggested-email-address", null, locale)));
          }
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return errorMessages;
  }
}
