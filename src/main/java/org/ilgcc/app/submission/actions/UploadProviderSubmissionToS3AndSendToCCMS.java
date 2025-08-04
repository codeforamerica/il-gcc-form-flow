package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.SubmissionSenderService;
import org.ilgcc.app.email.SendProviderConfirmationAfterResponseEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class UploadProviderSubmissionToS3AndSendToCCMS implements Action {

    private final SubmissionSenderService submissionSenderService;
    private final SendProviderConfirmationAfterResponseEmail sendProviderConfirmationAfterResponseEmail;

    public UploadProviderSubmissionToS3AndSendToCCMS(SubmissionSenderService submissionSenderService,
        SendProviderConfirmationAfterResponseEmail sendProviderConfirmationAfterResponseEmail) {
        this.submissionSenderService = submissionSenderService;
        this.sendProviderConfirmationAfterResponseEmail = sendProviderConfirmationAfterResponseEmail;
    }

    @Override
    public void run(Submission providerSubmission) {
        // If a provider is an existing provider that has done CCAP stuff before, send their submission to CCMS
        // New Provider Registration will send the application later
        if (!ProviderSubmissionUtilities.isProviderRegistering(providerSubmission)) {
            submissionSenderService.sendProviderSubmission(providerSubmission);
        }

        if (providerSubmission.getInputData().getOrDefault("providerResponseAgreeToCare", "false").equals("true")) {
            sendProviderConfirmationAfterResponseEmail.send(providerSubmission);
        }
    }
}