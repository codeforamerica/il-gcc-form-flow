package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HouseholdHasAtLeastOneChildWithoutAProvider implements Condition {
    private final boolean enableMultipleProviders;

    public HouseholdHasAtLeastOneChildWithoutAProvider(@Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.enableMultipleProviders = enableMultipleProviders;
    }
    @Override
    public Boolean run(Submission submission) {
        boolean hasOneChildOrMoreWithoutAProvider = submission.getInputData().getOrDefault("choseProviderForEveryChildInNeedOfCare", "").equals("false");
        return enableMultipleProviders && hasOneChildOrMoreWithoutAProvider;
    }
}
