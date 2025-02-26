package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.ilgcc.app.utils.ZipcodeOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ValidateZipCode implements Action {

    @Autowired
    MessageSource messageSource;

    public static final Locale locale = LocaleContextHolder.getLocale();

    private final String INPUT_NAME = "applicationZipCode";
    private static final String OUTPUT_NAME = "hasValidZipCode";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, List<String>> errorMessages = new java.util.HashMap<>(Collections.emptyMap());

        String providedZipCode = formSubmission.getFormData().get("applicationZipCode").toString();

        if (providedZipCode.isBlank() || !(providedZipCode.length() == 5)) {
            errorMessages.put(INPUT_NAME,
                    List.of(messageSource.getMessage("errors.provide-zip", null, locale)));
        } else {
            submission.getInputData()
                    .put(OUTPUT_NAME, String.valueOf(ZipcodeOption.isValidZipcodeOption(providedZipCode)));
        }

        return errorMessages;
    }

}