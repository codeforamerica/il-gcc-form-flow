package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import org.ilgcc.app.utils.AddressUtilities;

abstract class ShowAddressConfirmationScreen implements Condition {

    private final String addressGroupInputPrefix;

    public ShowAddressConfirmationScreen(String addressGroupInputPrefix) {
        this.addressGroupInputPrefix = addressGroupInputPrefix;
    }

    @Override
    public Boolean run(Submission submission) {
        return AddressUtilities.requireAddressConfirmation(submission, addressGroupInputPrefix);
    }
}
