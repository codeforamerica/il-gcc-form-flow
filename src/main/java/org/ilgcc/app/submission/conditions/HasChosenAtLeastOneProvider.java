package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SubmissionUtilities.hasChosenProvider;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HasChosenAtLeastOneProvider implements Condition {
    private final boolean enableMultipleProviders;

    public HasChosenAtLeastOneProvider(@Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.enableMultipleProviders = enableMultipleProviders;
    }
    @Override
    public Boolean run(Submission submission) {
        return enableMultipleProviders && hasChosenProvider(submission);
    }
}
