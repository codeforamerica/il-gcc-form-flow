package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SubmissionUtilities.isNoProviderSubmission;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class NoProviderChosen implements Condition {

    @Override
    public Boolean run(Submission submission) {
        return isNoProviderSubmission(submission.getInputData());
    }
    @Override
    public Boolean run(Submission submission, String UUID) {
        return isNoProviderSubmission(submission.getInputData());
    }
}
