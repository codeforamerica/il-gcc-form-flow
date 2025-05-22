package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class ContactProviderViaText implements Condition {

    @Value("${il-gcc.enable-multiple-providers}")
    private boolean enableMultipleProviders;

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        return SubmissionUtilities.isSelectedAsProviderContactMethod(inputData,"TEXT");
    }

    @Override
    public Boolean run(Submission submission, String subflowUuid) {
        Map<String, Object> inputData = submission.getInputData();
        return enableMultipleProviders && SubmissionUtilities.isSelectedAsProviderContactMethod(inputData, "TEXT");
    }
}
