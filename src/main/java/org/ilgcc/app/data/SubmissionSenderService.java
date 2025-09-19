package org.ilgcc.app.data;

import static org.ilgcc.app.utils.SubmissionUtilities.haveAllProvidersResponded;
import static org.ilgcc.app.utils.SubmissionUtilities.isPreMultiProviderApplicationWithSingleProvider;
import static org.ilgcc.app.utils.SubmissionUtilities.setCurrentProviderResponseInFamilyApplication;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SubmissionSenderService {

    private final SubmissionRepositoryService submissionRepositoryService;
    private final CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob;
    private final boolean enableMultipleProviders;

    public SubmissionSenderService(
            SubmissionRepositoryService submissionRepositoryService,
            CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob,
            @Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.submissionRepositoryService = submissionRepositoryService;
        this.ccmsSubmissionPayloadTransactionJob = ccmsSubmissionPayloadTransactionJob;
        this.enableMultipleProviders = enableMultipleProviders;
    }

    public void sendProviderSubmission(Submission providerSubmission) {
        sendProviderSubmission(providerSubmission, false, Optional.empty());
    }

    public void sendProviderSubmissionInstantly(Submission providerSubmission) {
        sendProviderSubmission(providerSubmission, true, Optional.empty());
    }

    public void sendProviderSubmissionInstantly(Submission providerSubmission, Optional<SendEmail> sendEmail) {
        sendProviderSubmission(providerSubmission, true, sendEmail);
    }

    private void sendProviderSubmission(Submission providerSubmission, boolean sendToCCMSInstantly, Optional<SendEmail> sendFamilyEmail) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId.get());
            if (familySubmissionOptional.isPresent()) {
                Submission familySubmission = familySubmissionOptional.get();

                log.info("Provider submitted response for family submission {}, enqueuing transfer of documents. Provider submission is {}",
                        familySubmission.getId(), providerSubmission.getId());

                if (enableMultipleProviders && !isPreMultiProviderApplicationWithSingleProvider(familySubmission)) {
                    setCurrentProviderResponseInFamilyApplication(providerSubmission, familySubmission);
                } else {
                    familySubmission.getInputData().put("providerResponseSubmissionId", providerSubmission.getId().toString());
                    familySubmission.getInputData().put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());
                }

                submissionRepositoryService.save(familySubmission);
                sendFamilyEmail.ifPresent(email -> email.send(familySubmission));


                if (haveAllProvidersResponded(familySubmission)) {
                    if (sendToCCMSInstantly) {
                        ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadInstantly(familySubmission.getId());
                    } else {
                        ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadWithDelay(familySubmission.getId());
                    }
                }
            } else {
                log.error("Could not find a family submission for id: {}", familySubmissionId.get());
            }
        } else {
            log.error("Family submission id is blank for the provider submission: {}", providerSubmission.getId());
        }
    }

    public void sendFamilySubmission(Submission familySubmission) {
        ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadWithDelay(familySubmission.getId());
    }
}
