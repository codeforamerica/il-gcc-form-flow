package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContactProviderViaEmail implements Condition {

    @Value("${il-gcc.enable-provider-messaging}")
    private boolean enableProviderMessaging;

    @Value("${il-gcc.enable-multiple-providers}")
    private boolean enableMultipleProviders;

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        return enableProviderMessaging && SubmissionUtilities.isSelectedAsProviderContactMethod(inputData, "EMAIL");
    }

    @Override
    public Boolean run(Submission submission, String subflowUuid) {
        Map<String, Object> inputData = submission.getInputData();
        return enableMultipleProviders && SubmissionUtilities.isSelectedAsProviderContactMethod(inputData, "EMAIL");
    }
}
