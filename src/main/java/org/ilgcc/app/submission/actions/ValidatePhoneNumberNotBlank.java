package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ValidatePhoneNumberNotBlank implements Action {

    @Autowired
    MessageSource messageSource;

    private final String PROVIDER_PHONE_NUMBER = "familyIntendedProviderPhoneNumber";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        String phoneNumber = (String) formSubmission.getFormData().getOrDefault(PROVIDER_PHONE_NUMBER, "");

        Locale locale = LocaleContextHolder.getLocale();
        if (phoneNumber.isBlank()) {
            errorMessages.put(PROVIDER_PHONE_NUMBER, List.of(messageSource.getMessage("errors.invalid-phone-number", null, locale)));
        }
        return errorMessages;
    }
}
