package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.hasProviderApplicationExpired;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendProviderRespondedConfirmationEmail;
import org.ilgcc.app.email.SendNewProviderAgreesToCareFamilyConfirmationEmail;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendNewProviderAndFamilyConfirmationEmails implements Action {

    private final SendNewProviderAgreesToCareFamilyConfirmationEmail sendNewProviderAgreesToCareFamilyConfirmationEmail;
    private final SendProviderRespondedConfirmationEmail sendProviderRespondedConfirmationEmail;
    private final SubmissionRepositoryService submissionRepositoryService;

    public SendNewProviderAndFamilyConfirmationEmails(
        SendNewProviderAgreesToCareFamilyConfirmationEmail sendNewProviderAgreesToCareFamilyConfirmationEmail,
        SendProviderRespondedConfirmationEmail sendProviderRespondedConfirmationEmail,
        SubmissionRepositoryService submissionRepositoryService) {
        this.sendNewProviderAgreesToCareFamilyConfirmationEmail = sendNewProviderAgreesToCareFamilyConfirmationEmail;
        this.sendProviderRespondedConfirmationEmail = sendProviderRespondedConfirmationEmail;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission providerSubmission) {
        if (hasProviderApplicationExpired(providerSubmission, submissionRepositoryService)) {
            sendNewProviderAgreesToCareFamilyConfirmationEmail.send(providerSubmission);
            sendProviderRespondedConfirmationEmail.send(providerSubmission);
        }
    }
}
