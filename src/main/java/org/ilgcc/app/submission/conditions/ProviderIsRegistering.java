package org.ilgcc.app.submission.conditions;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class ProviderIsRegistering extends BasicCondition {

    @Override
    public Boolean run(Submission submission) {
        return ProviderSubmissionUtilities.isProviderRegistering(submission);
    }
}
