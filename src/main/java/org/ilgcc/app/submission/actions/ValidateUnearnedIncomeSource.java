package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class ValidateUnearnedIncomeSource implements Action {

    @Autowired
    MessageSource messageSource;

    private final String UNEARNED_INCOME_SOURCE = "unearnedIncomeSource";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, List<String>> errorMessages = new HashMap<>();
        var inputData = (List<String>) formSubmission.getFormData().get(UNEARNED_INCOME_SOURCE + "[]");

        if (Objects.isNull(inputData) || inputData.isEmpty()) {
            errorMessages.put(UNEARNED_INCOME_SOURCE, List.of(messageSource.getMessage("unearned-income-source.field.required", null, locale)));
        }

        return errorMessages;
    }
}
