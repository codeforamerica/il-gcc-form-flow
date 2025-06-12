package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SubmissionUtilities.getChildrenNeedingAssistance;
import static org.ilgcc.app.utils.SubmissionUtilities.hasChosenProvider;
import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HasProviderForMoreThanOneChild implements Condition {
    private final boolean enableMultipleProviders;

    public HasProviderForMoreThanOneChild(@Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.enableMultipleProviders = enableMultipleProviders;
    }
    @Override
    public Boolean run(Submission submission) {
        boolean moreThanOneChildNeedsChildcareAssistance = getChildrenNeedingAssistance(submission.getInputData()).size() > 1;
        return enableMultipleProviders && hasChosenProvider(submission) && moreThanOneChildNeedsChildcareAssistance;
    }
}
