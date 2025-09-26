package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.hasProviderApplicationExpired;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.SubmissionSenderService;
import org.ilgcc.app.email.SendProviderNotIdentifiedFamilyEmail;
import org.ilgcc.app.email.SendUnidentifiedProviderConfirmationEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission implements Action {

    private final SendProviderNotIdentifiedFamilyEmail sendProviderNotIdentifiedFamilyEmail;
    private final SendUnidentifiedProviderConfirmationEmail sendUnidentifiedProviderConfirmationEmail;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final SubmissionSenderService submissionSenderService;

    public SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission(
            SubmissionSenderService submissionSenderService,
        SendProviderNotIdentifiedFamilyEmail sendProviderNotIdentifiedFamilyEmail,
        SendUnidentifiedProviderConfirmationEmail sendUnidentifiedProviderConfirmationEmail,
        SubmissionRepositoryService submissionRepositoryService) {

        this.submissionSenderService = submissionSenderService;
        this.sendProviderNotIdentifiedFamilyEmail = sendProviderNotIdentifiedFamilyEmail;
        this.sendUnidentifiedProviderConfirmationEmail = sendUnidentifiedProviderConfirmationEmail;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission providerSubmission) {
        // Because this is an unidentified provider, they don't get to upload documents and the job should be
        // enqueued instantly
        // If the providerSubmission has expired before this action has run, we should not send the emails linked to this action
        Optional<Submission> familySubmissionOptional = getFamilySubmission(providerSubmission, submissionRepositoryService);
        if (familySubmissionOptional.isPresent() && !hasProviderApplicationExpired(familySubmissionOptional.get(), providerSubmission)) {
            submissionSenderService.sendProviderSubmissionInstantly(providerSubmission,
                Optional.of(sendProviderNotIdentifiedFamilyEmail));

            sendUnidentifiedProviderConfirmationEmail.send(providerSubmission);
        }
    }

    private static Optional<Submission> getFamilySubmission(Submission providerSubmission, SubmissionRepositoryService submissionRepositoryService) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            return submissionRepositoryService.findById(familySubmissionId.get());
        }
        return Optional.empty();
    }
}
