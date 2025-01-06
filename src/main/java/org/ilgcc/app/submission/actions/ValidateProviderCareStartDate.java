package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ValidateProviderCareStartDate extends VerifyDate {

    @Autowired
    MessageSource messageSource;
    private final String INPUT_PREFIX = "providerCareStart";
    private final String INPUT_NAME = "providerCareStartDate";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, List<String>> errorMessages = new HashMap<>();

        String dateToValidate = joinDateComponentsToString(formSubmission, INPUT_PREFIX);

        if (this.isDateInvalid(dateToValidate)) {
            errorMessages.put(INPUT_NAME, List.of(messageSource.getMessage("errors.invalid-date-format", null, locale)));
        }

        if(this.isBeforeMinDate(dateToValidate)){
            errorMessages.put(INPUT_NAME, List.of(messageSource.getMessage("errors.invalid-min-date", null, locale)));
        }

        return errorMessages;
    }
}
