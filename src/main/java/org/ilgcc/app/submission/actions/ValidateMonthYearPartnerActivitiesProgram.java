package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ValidateMonthYearPartnerActivitiesProgram extends VerifyActivityDate {
    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        Map<String, Object> inputData = formSubmission.getFormData();
        DateFields start = new DateFields("partnerProgramStart", inputData);
        DateFields end = new DateFields("partnerProgramEnd", inputData);

        // Error checks for only months or only years present
        addErrorIfMonthWithoutYear(start, errorMessages);
        addErrorIfYearWithoutMonth(start, errorMessages);
        addErrorIfMonthWithoutYear(end, errorMessages);
        addErrorIfYearWithoutMonth(end, errorMessages);

        // Error checks for valid dates when present
        addErrorIfDateIsInvalid(start, errorMessages);
        addErrorIfDateIsInvalid(end, errorMessages);

        return errorMessages;
    }
}

