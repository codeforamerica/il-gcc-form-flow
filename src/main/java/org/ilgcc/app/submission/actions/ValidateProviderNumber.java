package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateProviderNumber implements Action {

    @Autowired
    MessageSource messageSource;

    private final String UNEARNED_INCOME_SOURCE = "providerResponseProviderNumber";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, List<String>> errorMessages = new HashMap<>();
        var inputData = (List<String>) formSubmission.getFormData().get(UNEARNED_INCOME_SOURCE + "[]");

//        if (Objects.isNull(inputData) || inputData.isEmpty()) {
            errorMessages.put(UNEARNED_INCOME_SOURCE, List.of(messageSource.getMessage("unearned-income-source.field.required", null, locale)));
//        }

        return errorMessages;
    }
}
