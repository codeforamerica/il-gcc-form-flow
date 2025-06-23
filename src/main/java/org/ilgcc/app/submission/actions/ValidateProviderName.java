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
public class ValidateProviderName implements Action {

    @Autowired
    MessageSource messageSource;
    
    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        Locale locale = LocaleContextHolder.getLocale();
        
        boolean providerIsIndividual = submission.getInputData().getOrDefault("providerType", "").equals("Individual");
        boolean providerIsCareProgram = submission.getInputData().getOrDefault("providerType", "").equals("Care Program");
        String providerFirstName = formSubmission.getFormData().getOrDefault("providerFirstName", "").toString();
        String providerLastName = formSubmission.getFormData().getOrDefault("providerLastName", "").toString();
        String providerCareProgramName = formSubmission.getFormData().getOrDefault("childCareProgramName", "").toString();
        
        if (providerIsIndividual) {
            if (providerFirstName.isBlank()) {
                errorMessages.put("providerFirstName", List.of(messageSource.getMessage("provider-name.enter-full-name", null, locale)));
            }
            if (providerLastName.isBlank()) {
                errorMessages.put("providerFirstName", List.of(messageSource.getMessage("provider-name.enter-full-name", null, locale)));
            }
        } 
        else if (providerIsCareProgram) {
            if (providerCareProgramName.isBlank()) {
                // If they don't enter a care program name, they need to enter both a first and last name
                if (providerFirstName.isBlank()) {
                    errorMessages.put("providerFirstName", List.of(messageSource.getMessage("providers-name.enter-one-or-other", null, locale)));
                }
                if (providerLastName.isBlank()) {
                    errorMessages.put("providerLastName", List.of(messageSource.getMessage("providers-name.enter-one-or-other", null, locale)));
                }
                if (providerFirstName.isBlank() && providerLastName.isBlank()) {
                    errorMessages.put("childCareProgramName", List.of(messageSource.getMessage("providers-name.enter-one-or-other", null, locale)));
                }
            }
        }
        return errorMessages;
    }
}
