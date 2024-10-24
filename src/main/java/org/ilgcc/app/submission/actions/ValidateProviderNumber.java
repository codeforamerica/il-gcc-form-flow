package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.ProviderRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateProviderNumber implements Action {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ProviderRepositoryService providerRepositoryService;

    private final String PROVIDER_NUMBER = "providerResponseProviderNumber";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        String inputValue = (String) formSubmission.getFormData().get(PROVIDER_NUMBER);

        if (!providerRepositoryService.isProviderIdValid(inputValue)) {
            Locale locale = LocaleContextHolder.getLocale();
            errorMessages.put(PROVIDER_NUMBER,
                    List.of(messageSource.getMessage("provider-response-ccap-registration.error.invalid-number", null, locale)));
        }

        return errorMessages;
    }
}
