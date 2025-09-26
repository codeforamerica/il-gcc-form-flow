package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class HouseholdHasAtLeastOneChildWithoutAProvider implements Condition {

    @Override
    public Boolean run(Submission submission) {
        boolean hasOneChildOrMoreWithoutAProvider = submission.getInputData()
                .getOrDefault("choseProviderForEveryChildInNeedOfCare", "").equals("false");
        return hasOneChildOrMoreWithoutAProvider;
    }
}
