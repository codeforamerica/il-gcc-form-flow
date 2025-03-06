package org.ilgcc.app.submission.conditions;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.AddressUtilities;
import org.springframework.stereotype.Component;
import formflow.library.config.submission.Condition;
@Component
public class ShowParentHomeAddressConfirmationScreen implements Condition {
    @Override
    public Boolean run(Submission submission) {
        return !AddressUtilities.skipAddressConfirmation(submission, "parentHome");
    }
}
