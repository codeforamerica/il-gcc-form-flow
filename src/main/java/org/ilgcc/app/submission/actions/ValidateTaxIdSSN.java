package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ValidateTaxIdSSN implements Action {
    @Autowired
    MessageSource messageSource;

    public static final Locale locale = LocaleContextHolder.getLocale();

    private static final String INPUT_NAME = "providerTaxIdSSN";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        String inputValue = (String) formSubmission.getFormData().get(INPUT_NAME);

        Locale locale = LocaleContextHolder.getLocale();

        String regex = "^\\d{3}-\\d{2}-\\d{4}"; 

        Pattern pattern = Pattern.compile(regex);
        Matcher requiredSSNMatcher = pattern.matcher(inputValue);

        if (!requiredSSNMatcher.matches()) {
            errorMessages.put(INPUT_NAME,
                    List.of(messageSource.getMessage("registration-home-provider-ssn.error", null, locale)));
        }

        return errorMessages;
    }

}