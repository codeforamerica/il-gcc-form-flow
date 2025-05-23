package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.submission.actions.ValidateProviderEmail.callSendGridAndValidateEmail;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import jakarta.servlet.http.HttpSession;
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
public class ValidateProviderEmailWhenInputIsPresent implements Action {

    private final HttpSession httpSession;

    @Autowired
    MessageSource messageSource;

    @Autowired
    SendGridEmailValidationService sendGridEmailValidationService;

    private final String INPUT_NAME = "familyIntendedProviderEmail";

    public ValidateProviderEmailWhenInputIsPresent(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

        Locale locale = LocaleContextHolder.getLocale();
        Map<String, List<String>> errorMessages = new HashMap<>();
        Map<String, Object> formData = formSubmission.getFormData();
        String providerEmail = formData.getOrDefault(INPUT_NAME, "").toString();

        if (providerEmail.isBlank()) {
            errorMessages.put(INPUT_NAME, List.of(messageSource.getMessage("errors.invalid-email.blank", null, locale)));
        }

        return callSendGridAndValidateEmail(locale, errorMessages, providerEmail, sendGridEmailValidationService, INPUT_NAME,
                messageSource, httpSession);
    }
}
