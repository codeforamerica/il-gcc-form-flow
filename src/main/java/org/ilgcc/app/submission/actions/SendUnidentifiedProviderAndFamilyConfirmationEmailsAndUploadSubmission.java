package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.SubmissionSenderService;
import org.ilgcc.app.email.SendProviderDidNotRespondToFamilyEmail;
import org.ilgcc.app.email.SendUnidentifiedProviderConfirmationEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission implements Action {

    @Autowired
    SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;

    @Autowired
    SendUnidentifiedProviderConfirmationEmail sendUnidentifiedProviderConfirmationEmail;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    private final SubmissionSenderService submissionSenderService;

    public SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission(
            SubmissionSenderService submissionSenderService) {

        this.submissionSenderService = submissionSenderService;
    }

    @Override
    public void run(Submission providerSubmission) {
        // Because this is an unidentified provider, they don't get to upload documents and the job should be
        // enqueued instantly
        // If the providerSubmission has expired before this action has run, we should not send the emails linked to this action
        if (providerSubmissionIsActive(providerSubmission)) {
            submissionSenderService.sendProviderSubmissionInstantly(providerSubmission,
                Optional.of(sendProviderDidNotRespondToFamilyEmail));

            sendUnidentifiedProviderConfirmationEmail.send(providerSubmission);
        }
    }

    public boolean providerSubmissionIsActive(Submission providerSubmission) {
        Optional<Submission> familySubmissionOptional = getFamilySubmission(providerSubmission);
        String currentProviderUuid = (String) providerSubmission.getInputData().getOrDefault("currentProviderUuid", "");
        if (familySubmissionOptional.isPresent() && !currentProviderUuid.isEmpty()) {
            return !(ProviderSubmissionUtilities.hasProviderApplicationExpired(familySubmissionOptional.get(), providerSubmission));
        }
        return true;
    }

    public Optional<Submission> getFamilySubmission(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            return submissionRepositoryService.findById(familySubmissionId.get());
        }
        return Optional.empty();
    }
}
