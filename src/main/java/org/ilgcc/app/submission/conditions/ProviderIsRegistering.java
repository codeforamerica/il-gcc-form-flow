package org.ilgcc.app.submission.conditions;

import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class ProviderIsRegistering extends BasicCondition {

    @Override
    public Boolean run(Submission submission) {
        // When a provider says they are not sure whether they applied for CCAP, the providerPaidCcap is not set but should be true
        return submission.getInputData().getOrDefault("providerPaidCcap", "false").toString().equals("false");
    }
}
