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
public class ValidateFEINIsNotBlank implements Action {

    @Autowired
    MessageSource messageSource;

    private final String FEIN = "providerTaxIdEIN";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        String fein = (String) formSubmission.getFormData().getOrDefault(FEIN, "");

        Locale locale = LocaleContextHolder.getLocale();
        if (fein.isBlank()) {
            errorMessages.put(FEIN, List.of(messageSource.getMessage("provider-id-fein.error-blank", null, locale)));
        }
        return errorMessages;
    }
}
