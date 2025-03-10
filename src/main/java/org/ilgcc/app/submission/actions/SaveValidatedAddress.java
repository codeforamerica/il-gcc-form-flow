package org.ilgcc.app.submission.actions;

import static formflow.library.inputs.FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATED;
import static org.ilgcc.app.utils.AddressUtilities.hasMatchingCity;
import static org.ilgcc.app.utils.AddressUtilities.hasMatchingState;
import static org.ilgcc.app.utils.AddressUtilities.hasMatchingStreetAddress;
import static org.ilgcc.app.utils.AddressUtilities.hasMatchingZipCode;
import static org.ilgcc.app.utils.AddressUtilities.suggestedAddressKey;
import static org.ilgcc.app.utils.AddressUtilities.useSuggestedAddressKey;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Map;

abstract class SaveValidatedAddress implements Action {

    private final SubmissionRepositoryService submissionRepositoryService;
    private final String addressGroupInputPrefix;

    public SaveValidatedAddress(SubmissionRepositoryService submissionRepositoryService, String addressGroupInputPrefix) {
        this.submissionRepositoryService = submissionRepositoryService;
        this.addressGroupInputPrefix = addressGroupInputPrefix;
    }

    @Override
    public void run(FormSubmission formSubmission, Submission submission) {
        boolean useValidatedAddress = formSubmission.getFormData()
                .getOrDefault(useSuggestedAddressKey(suggestedAddressKey, addressGroupInputPrefix), "false").equals("true");

        if (useValidatedAddress) {
            Map<String, Object> inputData = submission.getInputData();
            if (!hasMatchingStreetAddress(inputData, addressGroupInputPrefix)) {
                String addressKey = addressGroupInputPrefix + "StreetAddress1";
                submission = updateValue(submission, addressKey);
                submission.getInputData().put(addressGroupInputPrefix + "StreetAddress2", "");
            }

            if (!hasMatchingCity(inputData, addressGroupInputPrefix)) {
                String addressKey = addressGroupInputPrefix + "City";
                submission = updateValue(submission, addressKey);
            }

            if (!hasMatchingZipCode(inputData, addressGroupInputPrefix)) {
                String addressKey = addressGroupInputPrefix + "ZipCode";
                String addressValidatedKey = addressKey + UNVALIDATED_FIELD_MARKER_VALIDATED;
                String zipCodeValue = (String) submission.getInputData().getOrDefault(addressValidatedKey, "");
                if (!zipCodeValue.isBlank() && zipCodeValue.length() >= 5) {
                    submission.getInputData().put(addressKey, zipCodeValue.substring(0, 5));
                }
            }

            if (!hasMatchingState(inputData, addressGroupInputPrefix)) {
                String addressKey = addressGroupInputPrefix + "State";
                submission = updateValue(submission, addressKey);

            }

            submissionRepositoryService.save(submission);
        }
    }

    private Submission updateValue(Submission submission, String key) {
        String addressValidatedKey = key + UNVALIDATED_FIELD_MARKER_VALIDATED;
        submission.getInputData().put(key, submission.getInputData().get(addressValidatedKey));
        return submission;
    }
}


