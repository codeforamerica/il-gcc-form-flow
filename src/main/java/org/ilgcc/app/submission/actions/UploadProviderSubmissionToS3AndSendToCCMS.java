package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.SubmissionSenderService;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UploadProviderSubmissionToS3AndSendToCCMS implements Action {

    SubmissionRepositoryService submissionRepositoryService;

    private final SubmissionSenderService submissionSenderService;

    public UploadProviderSubmissionToS3AndSendToCCMS(SubmissionSenderService submissionSenderService,
            SubmissionRepositoryService submissionRepositoryService) {
        this.submissionSenderService = submissionSenderService;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission providerSubmission) {
        Optional<Submission> familySubmissionOptional = getFamilySubmission(providerSubmission, submissionRepositoryService);
        if (familySubmissionOptional.isPresent()) {
            // Prevent sending provider submissions if a providers application has expired.
            if (!ProviderSubmissionUtilities.hasProviderApplicationExpired(familySubmissionOptional.get(), providerSubmission)) {
                // If a provider is an existing provider that has done CCAP stuff before, send their submission to CCMS
                // New Provider Registration will send the application later
                if (!ProviderSubmissionUtilities.isProviderRegistering(providerSubmission)) {
                    submissionSenderService.sendProviderSubmission(providerSubmission);
                }
            } else {
                log.info("Provider submission {} expired for family submission {}", providerSubmission.getId(), familySubmissionOptional.get().getId());
            }
        } else {
            log.error("Family submission not found for provider submission: " + providerSubmission.getId());
        }
    }

    private static Optional<Submission> getFamilySubmission(Submission providerSubmission,
            SubmissionRepositoryService submissionRepositoryService) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            return submissionRepositoryService.findById(familySubmissionId.get());
        }
        return Optional.empty();
    }
}