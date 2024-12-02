package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class IsPaidByCCAP extends BasicCondition {

    @Override
    public Boolean run(Submission submission) {
        return run(submission, "providerPaidCcap", "true");
    }
}
