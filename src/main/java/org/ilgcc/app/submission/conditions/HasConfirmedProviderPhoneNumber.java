package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class HasConfirmedProviderPhoneNumber implements Condition {

    @Override
    public Boolean run(Submission submission) {
        return confirmedPhoneNumber(submission);
    }

    private boolean confirmedPhoneNumber(Submission submission) {
        return submission.getInputData().getOrDefault("hasConfirmedIntendedProviderPhoneNumber", "false").equals("true");
    }
}
