package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.submission.router.ApplicationRoutingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ValidateHomeAddressZipCode implements Action {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ApplicationRoutingServiceImpl applicationRoutingService;
    public static final Locale locale = LocaleContextHolder.getLocale();

    private final String INPUT_NAME = "parentHomeZipCode";
    private static final String OUTPUT_NAME = "hasValidHomeAddressZipCode";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, List<String>> errorMessages = new java.util.HashMap<>(Collections.emptyMap());

        Optional<ResourceOrganization> resourceOrganizationOptional = Optional.empty();
        String providedZipCode = formSubmission.getFormData().get(INPUT_NAME).toString();
        if (!providedZipCode.isBlank() && (providedZipCode.length() == 5)) {
            resourceOrganizationOptional = applicationRoutingService.getOrganizationIdByZipCode(providedZipCode);
            if (resourceOrganizationOptional.isEmpty()) {
                errorMessages.put(INPUT_NAME,
                        List.of(messageSource.getMessage("errors.out-of-state-zip", null, locale)));
            }
        } else {
            errorMessages.put(INPUT_NAME,
                    List.of(messageSource.getMessage("errors.provide-zip", null, locale)));
        }
        
        return errorMessages;
    }
}