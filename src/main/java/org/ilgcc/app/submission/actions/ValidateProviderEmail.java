package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_INVALID_PROVIDER_EMAIL;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.utils.RegexUtils;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.sendgrid.SendGridEmailValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateProviderEmail implements Action {

  private final HttpSession httpSession;
  @Autowired
  MessageSource messageSource;

  @Autowired
  SendGridEmailValidationService sendGridEmailValidationService;
  private final String INPUT_NAME = "familyIntendedProviderEmail";
  public ValidateProviderEmail(HttpSession httpSession) {this.httpSession = httpSession; }
  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

    Locale locale = LocaleContextHolder.getLocale();
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> formData = formSubmission.getFormData();
    String providerEmail = formData.getOrDefault(INPUT_NAME, "").toString();

    return callSendGridAndValidateEmail(locale, errorMessages, providerEmail, sendGridEmailValidationService, INPUT_NAME,
        messageSource, httpSession);
  }

  static Map<String, List<String>> callSendGridAndValidateEmail(Locale locale, Map<String, List<String>> errorMessages,
      String providerEmail, SendGridEmailValidationService sendGridEmailValidationService, String inputName,
      MessageSource messageSource, HttpSession httpSession) {
    if (providerEmail.matches(RegexUtils.EMAIL_REGEX)) {
      try {
        HashMap<String, String> emailValidationResult = sendGridEmailValidationService.validateEmail(providerEmail);
        if (emailValidationResult.getOrDefault("endpointReached", "").equals("success")) {
          if (emailValidationResult.get("emailIsValid").equals("true")) {
            return errorMessages;
          } else {
            if(httpSession.getAttribute(SESSION_KEY_INVALID_PROVIDER_EMAIL) != null && httpSession.getAttribute(SESSION_KEY_INVALID_PROVIDER_EMAIL).equals(providerEmail)) {
              return errorMessages;
            }
            if (emailValidationResult.get("hasSuggestion").equals("true")) {
              errorMessages.put(inputName, List.of(messageSource.getMessage("errors.invalid-email.with-suggested-email-address",
                  new Object[]{emailValidationResult.get("suggestedEmail")}, locale)));

            } else {
              errorMessages.put(inputName,
                  List.of(messageSource.getMessage("errors.invalid-email.no-suggested-email-address", null, locale)));
            }
            httpSession.setAttribute(SESSION_KEY_INVALID_PROVIDER_EMAIL, providerEmail);
          }
        }

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return errorMessages;
  }
}
