package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.ilgcc.app.data.ProviderRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ValidateProviderLanguageInput implements Action {

    @Autowired
    MessageSource messageSource;

    private final String PROVIDER_LANGUAGE = "providerLanguageOffered";
    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        String inputValue = (String) formSubmission.getFormData().getOrDefault("providerLanguageOffered[]", "");

        Locale locale = LocaleContextHolder.getLocale();

        if (inputValue.isEmpty()) {
            errorMessages.put("providerLanguageOffered[]",
                    List.of(messageSource.getMessage("registration-service-languages.error", null, locale)));
        }
        return errorMessages;
    }
}
