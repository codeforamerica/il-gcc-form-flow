package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import org.ilgcc.app.data.ProviderRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ValidateProviderResponsePhoneAndEmail implements Action {

    @Autowired
    MessageSource messageSource;

    ProviderRepositoryService providerRepositoryService;

    public ValidateProviderResponsePhoneAndEmail(ProviderRepositoryService providerRepositoryService) {
        this.providerRepositoryService = providerRepositoryService;
    }

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
        return errorMessages;
    }
}
