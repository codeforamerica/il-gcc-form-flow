package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.providerSubmissionIsActive;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.SubmissionSenderService;
import org.ilgcc.app.email.SendProviderDidNotRespondToFamilyEmail;
import org.ilgcc.app.email.SendUnidentifiedProviderConfirmationEmail;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission implements Action {

    private final SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;
    private final SendUnidentifiedProviderConfirmationEmail sendUnidentifiedProviderConfirmationEmail;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final SubmissionSenderService submissionSenderService;

    public SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission(
            SubmissionSenderService submissionSenderService,
        SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail,
        SendUnidentifiedProviderConfirmationEmail sendUnidentifiedProviderConfirmationEmail,
        SubmissionRepositoryService submissionRepositoryService) {

        this.submissionSenderService = submissionSenderService;
        this.sendProviderDidNotRespondToFamilyEmail = sendProviderDidNotRespondToFamilyEmail;
        this.sendUnidentifiedProviderConfirmationEmail = sendUnidentifiedProviderConfirmationEmail;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission providerSubmission) {
        // Because this is an unidentified provider, they don't get to upload documents and the job should be
        // enqueued instantly
        // If the providerSubmission has expired before this action has run, we should not send the emails linked to this action
        if (providerSubmissionIsActive(providerSubmission, submissionRepositoryService)) {
            submissionSenderService.sendProviderSubmissionInstantly(providerSubmission,
                Optional.of(sendProviderDidNotRespondToFamilyEmail));

            sendUnidentifiedProviderConfirmationEmail.send(providerSubmission);
        }
    }
}
