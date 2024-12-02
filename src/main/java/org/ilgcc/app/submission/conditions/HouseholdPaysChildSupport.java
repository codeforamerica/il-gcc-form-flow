package org.ilgcc.app.submission.conditions;

import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class HouseholdPaysChildSupport extends BasicCondition {

    @Override
    public Boolean run(Submission submission) {
        return run(submission, "doesAnyoneInHouseholdPayChildSupport", "true");
    }

}
