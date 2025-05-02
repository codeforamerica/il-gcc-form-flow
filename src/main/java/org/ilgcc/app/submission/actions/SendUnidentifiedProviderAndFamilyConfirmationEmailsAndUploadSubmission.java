package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.SubmissionSenderService;
import org.ilgcc.app.email.SendProviderDidNotRespondToFamilyEmail;
import org.ilgcc.app.email.SendUnidentifiedProviderConfirmationEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission implements Action {

    @Autowired
    SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;

    @Autowired
    SendUnidentifiedProviderConfirmationEmail sendUnidentifiedProviderConfirmationEmail;

    private final SubmissionSenderService submissionSenderService;

    public SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission(
            SubmissionSenderService submissionSenderService) {

        this.submissionSenderService = submissionSenderService;
    }

    @Override
    public void run(Submission providerSubmission) {
        // Because this is an unidentified provider, they don't get to upload documents and the job should be
        // enqueued instantly
        submissionSenderService.sendProviderSubmissionInstantly(providerSubmission,
                Optional.of(sendProviderDidNotRespondToFamilyEmail));

        sendUnidentifiedProviderConfirmationEmail.send(providerSubmission);
    }
}
