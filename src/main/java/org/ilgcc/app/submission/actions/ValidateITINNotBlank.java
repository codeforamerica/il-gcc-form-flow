package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ValidateITINNotBlank implements Action {

        @Autowired
        MessageSource messageSource;

        public static final Locale locale = LocaleContextHolder.getLocale();

        private final String INPUT_NAME = "providerITIN";

        @Override
        public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {

            Map<String, List<String>> errorMessages = new HashMap<>();
            String inputValue = formSubmission.getFormData().getOrDefault(INPUT_NAME, "").toString();

            if (inputValue.isBlank()) {
                errorMessages.put(INPUT_NAME,
                        List.of(messageSource.getMessage("registration-home-provider-itin.error-blank", null, locale)));
            }

            return errorMessages;
        }
}
