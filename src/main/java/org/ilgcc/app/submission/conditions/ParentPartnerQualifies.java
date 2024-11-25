package org.ilgcc.app.submission.conditions;

import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ParentPartnerQualifies extends BasicCondition {
    @Override
    public Boolean run(Submission submission) {
        return run(submission, "parentHasPartner", "true") && run(submission, "parentHasQualifyingPartner", "true");
    }
}