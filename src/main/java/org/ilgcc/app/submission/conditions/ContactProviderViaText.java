package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContactProviderViaText implements Condition {

    private final boolean enableMultipleProviders;

    public ContactProviderViaText(@Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.enableMultipleProviders = enableMultipleProviders;
    }

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        return SubmissionUtilities.isSelectedAsProviderContactMethod(inputData, "TEXT");
    }

    @Override
    public Boolean run(Submission submission, String subflowUuid) {
        Map<String, Object> inputData = submission.getInputData();
        return enableMultipleProviders && SubmissionUtilities.isSelectedAsProviderContactMethod(inputData, "TEXT");
    }
}
