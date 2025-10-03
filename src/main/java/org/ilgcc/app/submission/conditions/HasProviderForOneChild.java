package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SubmissionUtilities.getChildrenNeedingAssistance;
import static org.ilgcc.app.utils.SubmissionUtilities.hasChosenProvider;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HasProviderForOneChild implements Condition {
    @Override
    public Boolean run(Submission submission) {
        boolean oneChildNeedsChildcareAssistance = getChildrenNeedingAssistance(submission.getInputData()).size() == 1;
        return hasChosenProvider(submission) && oneChildNeedsChildcareAssistance;
    }
}
