package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.DateUtilities;
import org.springframework.stereotype.Component;

@Component
public class CreateProviderCareStartDate extends VerifyDate {
    private final String INPUT_PREFIX = "providerCareStart";
    private final String INPUT_NAME = "providerCareStartDate";

    @Override
    public void run(FormSubmission formSubmission, Submission submission) {
        Map<String, Object> inputData = formSubmission.getFormData();
        String formattedDate = DateUtilities.getFormattedDateFromMonthDateYearInputs(INPUT_PREFIX, inputData);

        formSubmission.formData.put(INPUT_NAME, formattedDate);
    }
}