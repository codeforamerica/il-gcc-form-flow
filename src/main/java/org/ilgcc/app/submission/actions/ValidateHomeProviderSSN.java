package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ValidateHomeProviderSSN implements Action {
    @Autowired
    MessageSource messageSource;

    @Autowired
    Environment env;

    public static final Locale locale = LocaleContextHolder.getLocale();

    private final String INPUT_NAME = "providerIdentityCheckSSN";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        String inputValue = (String) formSubmission.getFormData().get(INPUT_NAME);

        Locale locale = LocaleContextHolder.getLocale();

        List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        boolean useWeakerValidation = activeProfiles.contains("dev") || activeProfiles.contains("staging");
        String regex = useWeakerValidation ? "^\\d{3}-\\d{2}-\\d{4}" : "^(?!000|666|9\\d{2})\\d{3}-(?!00)\\d{2}-(?!0000)\\d{4}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher requiredSSNMatcher = pattern.matcher(inputValue);

        if (!requiredSSNMatcher.matches()) {
            errorMessages.put(INPUT_NAME,
                    List.of(messageSource.getMessage("errors.invalid-ssn", null, locale)));
        }

        return errorMessages;
    }

}