package org.ilgcc.app.submission.actions;

import static formflow.library.inputs.FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATED;
import static org.ilgcc.app.utils.AddressUtilities.addressKeys;
import static org.ilgcc.app.utils.AddressUtilities.suggestedAddressKey;
import static org.ilgcc.app.utils.AddressUtilities.useSuggestedAddressKey;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;

public class SaveValidatedAddress implements Action {

    private static SubmissionRepositoryService submissionRepositoryService;
    private static String addressGroupInputPrefix;

    public SaveValidatedAddress(SubmissionRepositoryService submissionRepositoryService, String addressGroupInputPrefix) {
        this.submissionRepositoryService = submissionRepositoryService;
        this.addressGroupInputPrefix = addressGroupInputPrefix;
    }

    @Override
    public void run(FormSubmission formSubmission, Submission submission) {
        var useSmartyValidatedAddress = submission.getInputData()
                .getOrDefault(useSuggestedAddressKey(suggestedAddressKey, addressGroupInputPrefix), "false").equals("true");

        if (useSmartyValidatedAddress) {
            addressKeys.stream().forEach(key -> {
                String addressKey =  addressGroupInputPrefix + key;
                String addressValidatedKey = addressKey + UNVALIDATED_FIELD_MARKER_VALIDATED;
                submission.getInputData().put(addressKey, submission.getInputData().get(addressValidatedKey));
            });
            submissionRepositoryService.save(submission);
        }
    }

}
