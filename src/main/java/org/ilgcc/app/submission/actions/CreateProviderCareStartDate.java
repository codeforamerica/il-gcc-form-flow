package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class CreateProviderCareStartDate extends VerifyDate {
    private final String INPUT_PREFIX = "providerCareStart";
    private final String INPUT_NAME = "providerCareStartDate";

    @Override
    public void run(FormSubmission formSubmission, Submission submission) {

        String formattedDate = joinDateComponentsToString(formSubmission, INPUT_PREFIX);

        formSubmission.formData.put(INPUT_NAME, formattedDate);
    }
}
