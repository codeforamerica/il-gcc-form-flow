package org.ilgcc.app.submission.conditions;

import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class ProviderAgreedToCare extends BasicCondition {

    @Override
    public Boolean run(Submission submission) {
        return run(submission, "providerResponseAgreeToCare", "true");
    }
}
