package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.utils.RegexUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.sendgrid.SendGridEmailValidationService;
import org.ilgcc.app.utils.SendGridUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.ilgcc.app.inputs.InputsConstants.PHONE_REGEX;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_INVALID_PARENT_PARTNER_EMAIL;

@Slf4j
@Component
public class ValidateParentPartnerContactEmailPhone implements Action {

  @Autowired
  MessageSource messageSource;

  @Autowired
  HttpSession httpSession;

  @Autowired
  SendGridEmailValidationService sendGridEmailValidationService;

  private final String INPUT_NAME_EMAIL = "parentPartnerEmail";
  private final String INPUT_NAME_PHONE = "parentPartnerPhoneNumber";

  @Override
  public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

    Locale locale = LocaleContextHolder.getLocale();
    Map<String, List<String>> errorMessages = new HashMap<>();
    Map<String, Object> formData = formSubmission.getFormData();
    String partnerEmail = formData.getOrDefault(INPUT_NAME_EMAIL,"").toString();
    String partnerPhone = formData.getOrDefault(INPUT_NAME_PHONE,"").toString();

    if (!partnerPhone.isBlank() && !partnerPhone.matches(PHONE_REGEX)) {
      errorMessages.put(INPUT_NAME_PHONE, List.of(messageSource.getMessage("errors.invalid-phone-number", null, locale)));
    }

    if (!partnerEmail.isBlank()){
      if (!partnerEmail.matches(RegexUtils.EMAIL_REGEX)) {
        errorMessages.put(INPUT_NAME_EMAIL, List.of(messageSource.getMessage("errors.invalid-email", null, locale)));
      } else{
        SendGridUtilities.callSendGridAndValidateEmail(locale, errorMessages, partnerEmail, sendGridEmailValidationService, INPUT_NAME_EMAIL,
                messageSource, httpSession,SESSION_KEY_INVALID_PARENT_PARTNER_EMAIL,true);
      }
    }

    return errorMessages;
  }
}
