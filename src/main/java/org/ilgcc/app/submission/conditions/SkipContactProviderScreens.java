package org.ilgcc.app.submission.conditions;


import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SkipContactProviderScreens implements Condition {
    private final boolean enableMultipleProviders;

    public SkipContactProviderScreens(@Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.enableMultipleProviders = enableMultipleProviders;
    }
    @Override
    public Boolean run(Submission submission) {
        boolean hasNoProviderWithChildSchedule = SubmissionUtilities.hasNotChosenAProviderOrHasNoProvidersScheduled(submission);
        return enableMultipleProviders && hasNoProviderWithChildSchedule;
    }
}
