package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.utils.RegexUtils;
import jakarta.servlet.http.HttpSession;
import org.ilgcc.app.data.ProviderRepositoryService;
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
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_INVALID_PROVIDER_RESPONSE_CONTACT_EMAIL;

@Component
public class ValidateProviderResponsePhoneAndEmail implements Action {

    @Autowired
    MessageSource messageSource;

    @Autowired
    HttpSession httpSession;

    @Autowired
    SendGridEmailValidationService sendGridEmailValidationService;

    private final String EMAIL = "providerResponseContactEmail";
    private final String PHONE = "providerResponseContactPhoneNumber";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        String email = (String) formSubmission.getFormData().getOrDefault(EMAIL, "");
        String phone = (String) formSubmission.getFormData().getOrDefault(PHONE, "");

        Locale locale = LocaleContextHolder.getLocale();
        if (email.isBlank() && phone.isBlank()) {
            errorMessages.put(EMAIL, List.of(messageSource.getMessage("errors.require-email", null, locale)));
            errorMessages.put(PHONE, List.of(messageSource.getMessage("errors.invalid-phone-number", null, locale)));
        }
        if (!phone.isBlank() && !phone.matches(PHONE_REGEX)) {
            errorMessages.put(PHONE, List.of(messageSource.getMessage("errors.invalid-phone-number", null, locale)));
        }
        if (!email.isBlank()) {
            if(!email.matches(RegexUtils.EMAIL_REGEX)){
                errorMessages.put(EMAIL, List.of(messageSource.getMessage("errors.invalid-email.no-suggested-email-address", null, locale)));
            }else{
                SendGridUtilities.callSendGridAndValidateEmail(locale, errorMessages, email, sendGridEmailValidationService, EMAIL,
                        messageSource, httpSession,SESSION_KEY_INVALID_PROVIDER_RESPONSE_CONTACT_EMAIL);
            }

        }
        return errorMessages;
    }
}
