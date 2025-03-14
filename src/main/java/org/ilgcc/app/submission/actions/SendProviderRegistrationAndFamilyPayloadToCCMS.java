package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SendProviderRegistrationAndFamilyPayloadToCCMS implements Action {
    
    private final SubmissionRepositoryService submissionRepositoryService;
    private final CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob;
    private final boolean CCMS_INTEGRATION_ENABLED;

    public SendProviderRegistrationAndFamilyPayloadToCCMS(
            SubmissionRepositoryService submissionRepositoryService,
            CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob,
            @Value("${il-gcc.ccms-integration-enabled:false}") boolean ccmsIntegrationEnabled) {
        this.submissionRepositoryService = submissionRepositoryService;
        this.ccmsSubmissionPayloadTransactionJob = ccmsSubmissionPayloadTransactionJob;
        CCMS_INTEGRATION_ENABLED = ccmsIntegrationEnabled;
    }

    @Override
    public void run(Submission providerSubmission) {
        var familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId.get());
            if (familySubmissionOptional.isPresent()) {
                log.info("Provider submitted response for family submission {}, enqueuing transfer of documents.",
                        familySubmissionId.get());
                Submission familySubmission = familySubmissionOptional.get();
                familySubmission.getInputData().put("providerResponseSubmissionId", providerSubmission.getId().toString());
                submissionRepositoryService.save(familySubmission);
                if (CCMS_INTEGRATION_ENABLED) {
                    ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadWithDelay(familySubmission);
                }
            } else {
                log.error("We can not find a match for your family submission: {}", familySubmissionId.get());
            }
        } else {
            log.error("Family Submission Id is Blank for the provider submission: {}.", providerSubmission.getId().toString());
        }
    }
}