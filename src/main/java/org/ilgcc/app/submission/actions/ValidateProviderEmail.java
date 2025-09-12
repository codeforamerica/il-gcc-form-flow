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
import org.ilgcc.app.utils.SendGridUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateProviderEmail implements Action {

  @Autowired
  HttpSession httpSession;

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
    String providerEmail = formData.getOrDefault(INPUT_NAME, "").toString();

    SendGridUtilities.callSendGridAndValidateEmail(locale, errorMessages, providerEmail, sendGridEmailValidationService, INPUT_NAME,
        messageSource, httpSession,SESSION_KEY_INVALID_PROVIDER_EMAIL,false);
    return errorMessages;
  }
}
