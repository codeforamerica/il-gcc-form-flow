package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.inputs.InputsConstants.FEIN_REGEX;

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
public class ValidateFEIN implements Action {

    @Autowired
    MessageSource messageSource;

    ProviderRepositoryService providerRepositoryService;

    public ValidateFEIN(ProviderRepositoryService providerRepositoryService) {
        this.providerRepositoryService = providerRepositoryService;
    }

    private final String FEIN = "providerTaxIdFEIN";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        String fein = (String) formSubmission.getFormData().getOrDefault(FEIN, "");

        Locale locale = LocaleContextHolder.getLocale();
        if (fein.isBlank()) {
            errorMessages.put(FEIN, List.of(messageSource.getMessage("provider-id-fein.error-blank", null, locale)));
        } else {
            if (!fein.matches(FEIN_REGEX)) {
                errorMessages.put(FEIN, List.of(messageSource.getMessage("provider-id-fein.error-invalid-format", null, locale)));
            }
//            else if (!providerRepositoryService.isFEINValid(fein)) {
//                errorMessages.put(FEIN, List.of(messageSource.getMessage("provider-id-fein.error-valid-fein-not-found", null, locale)));
//            }
        }
        return errorMessages;
    }
}
