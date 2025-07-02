package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SendProviderRegistrationAndFamilyPayloadToCCMS implements Action {

    private final SubmissionRepositoryService submissionRepositoryService;
    private final CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob;
    private final boolean ccmsIntegrationEnabled;
    private final boolean multipleProvidersEnabled;

    public SendProviderRegistrationAndFamilyPayloadToCCMS(
            SubmissionRepositoryService submissionRepositoryService,
            CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob,
            @Value("${il-gcc.ccms-integration-enabled:false}") boolean ccmsIntegrationEnabled,
            @Value("${il-gcc.enable-multiple-providers}") boolean multipleProvidersEnabled) {
        this.submissionRepositoryService = submissionRepositoryService;
        this.ccmsSubmissionPayloadTransactionJob = ccmsSubmissionPayloadTransactionJob;
        this.ccmsIntegrationEnabled = ccmsIntegrationEnabled;
        this.multipleProvidersEnabled = multipleProvidersEnabled;
    }

    @Override
    public void run(Submission providerSubmission) {
        Optional<UUID> familySubmissionIdOptional = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionIdOptional.isPresent()) {
            UUID familySubmissionId = familySubmissionIdOptional.get();
            Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId);
            if (familySubmissionOptional.isPresent()) {
                Submission familySubmission = familySubmissionOptional.get();

                if (multipleProvidersEnabled) {
                    SubmissionUtilities.respondForCurrentProvider(providerSubmission, familySubmission);
                    submissionRepositoryService.save(familySubmission);

                    if (SubmissionUtilities.isFamilyApplicationFullyRespondedTo(familySubmission)) {
                        log.info("New Provider submitted response for family submission {}, enqueuing transfer of documents because all providers responded.",
                                familySubmissionId);
                        if (ccmsIntegrationEnabled) {
                            ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadWithDelay(familySubmission.getId());
                        }
                    } else {
                        log.info("New Provider submitted response for family submission {}, skipping transfer of documents because all providers have not responded.",
                                familySubmissionId);
                    }
                } else {
                    log.info("New Provider submitted response for family submission {}, enqueuing transfer of documents.",
                            familySubmissionId);
                    familySubmission.getInputData().put("providerResponseSubmissionId", providerSubmission.getId().toString());
                    familySubmission.getInputData().put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());
                    submissionRepositoryService.save(familySubmission);
                    if (ccmsIntegrationEnabled) {
                        ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadWithDelay(familySubmissionId);
                    }
                }
            } else {
                log.error("We can not find a match for your family submission: {}", familySubmissionId);
            }
        } else {
            log.error("Family Submission Id is Blank for the provider submission: {}.", providerSubmission.getId());
        }
    }
}