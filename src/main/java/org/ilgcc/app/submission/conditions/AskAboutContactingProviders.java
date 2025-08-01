package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SubmissionUtilities.isNoProviderSubmission;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AskAboutContactingProviders implements Condition {
    private final boolean enableMultipleProviders;

    public AskAboutContactingProviders(@Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.enableMultipleProviders = enableMultipleProviders;
    }
    @Override
    public Boolean run(Submission submission) {
        return enableMultipleProviders && !isNoProviderSubmission(submission.getInputData());
    }
}
