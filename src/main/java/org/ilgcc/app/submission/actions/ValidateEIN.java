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
public class ValidateEIN implements Action {
    @Autowired
    MessageSource messageSource;

    public static final Locale locale = LocaleContextHolder.getLocale();

    private static final String INPUT_NAME = "providerTaxIdEIN";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        String inputValue = (String) formSubmission.getFormData().get(INPUT_NAME);

        Locale locale = LocaleContextHolder.getLocale();

        String regex = "\\d{9}";

        Pattern pattern = Pattern.compile(regex);
        Matcher requiredEINMatcher = pattern.matcher(inputValue);

        if (!requiredEINMatcher.matches()) {
            errorMessages.put(INPUT_NAME,
                    List.of(messageSource.getMessage("registration-tax-id-ein.error", null, locale)));
        }

        return errorMessages;
    }

}