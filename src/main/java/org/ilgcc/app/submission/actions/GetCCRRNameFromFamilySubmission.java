package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getFamilySubmissionId;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetCCRRNameFromFamilySubmission implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    private Optional<Submission> familySubmissionOptional;
    private static final String CCRR_NAME_INPUT = "ccrrName";
    private static final String CCRR_PHONE_NUMBER_INPUT = "ccrrPhoneNumber";

    @Override
    public void run(Submission providerSubmission) {
        Map<String, Object> providerInputData = providerSubmission.getInputData();
        //get family submission from provider submission
        Optional<UUID> familySubmissionID = getFamilySubmissionId(providerSubmission);

        familySubmissionID.ifPresent(familySubmissionId -> {
            familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId);
            if (familySubmissionOptional.isPresent()) {
                Map<String, Object> familyInputData = familySubmissionOptional.get().getInputData();
                if (familyInputData.containsKey(CCRR_NAME_INPUT)) {
                    providerInputData.put(CCRR_NAME_INPUT, familyInputData.getOrDefault(CCRR_NAME_INPUT, ""));
                } else {
                    log.error("Could not find CCR&R name for the familySubmissionId: {}", familySubmissionID);
                }
                if (familyInputData.containsKey(CCRR_PHONE_NUMBER_INPUT)) {
                    providerInputData.put(CCRR_PHONE_NUMBER_INPUT, familyInputData.getOrDefault(CCRR_PHONE_NUMBER_INPUT, ""));
                } else {
                    log.error("Could not find CCR&R phone number for the familySubmissionId: {}", familySubmissionID);
                }
            } else {
                log.error("Could not find submission for the familySubmissionId: {}", familySubmissionID);
            }
        });
    }
}

