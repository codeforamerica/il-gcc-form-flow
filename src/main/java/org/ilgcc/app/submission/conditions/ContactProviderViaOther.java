package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContactProviderViaOther implements Condition {

    @Value("${il-gcc.enable-provider-messaging}")

    private boolean enableProviderMessaging;

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        return enableProviderMessaging && SubmissionUtilities.isSelectedAsProviderContactMethod(inputData, "OTHER");
    }
}