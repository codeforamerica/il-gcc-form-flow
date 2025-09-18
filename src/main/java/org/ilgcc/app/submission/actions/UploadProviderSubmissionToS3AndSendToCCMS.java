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
        // If a provider is an existing provider that has done CCAP stuff before, send their submission to CCMS
        // New Provider Registration will send the application later
        // Prevent sending provider submissions if a providers application has expired.
        boolean hasProviderApplicationExpired = false;
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId.get());
            if (familySubmissionOptional.isPresent()) {
                Submission familySubmission = familySubmissionOptional.get();
                hasProviderApplicationExpired = ProviderSubmissionUtilities.hasProviderApplicationExpired(familySubmission, providerSubmission);
            }
        }

        if (!hasProviderApplicationExpired) {
            if (!ProviderSubmissionUtilities.isProviderRegistering(providerSubmission)) {
                submissionSenderService.sendProviderSubmission(providerSubmission);
            }
        }else {
            log.error("Your provider submission {} expired.", providerSubmission.getId());
        }
    }
}