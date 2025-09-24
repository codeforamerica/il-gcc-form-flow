package org.ilgcc.app.submission.actions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendProviderAgreesToCareFamilyConfirmationEmail;
import org.ilgcc.app.email.SendProviderRespondedConfirmationEmail;
import org.ilgcc.app.email.SendProviderDeclinesCareFamilyConfirmationEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.stereotype.Component;
import formflow.library.config.submission.Action;
@Component
@Slf4j
public class SendProviderAndFamilyEmails implements Action {

    private final SendProviderRespondedConfirmationEmail sendProviderRespondedConfirmationEmail;
    private final SendProviderAgreesToCareFamilyConfirmationEmail sendProviderAgreesToCareFamilyConfirmationEmail;
    private final SendProviderDeclinesCareFamilyConfirmationEmail sendProviderDeclinesCareFamilyConfirmationEmail;
    private final SubmissionRepositoryService submissionRepositoryService;

    public SendProviderAndFamilyEmails(SendProviderRespondedConfirmationEmail sendProviderRespondedConfirmationEmail,
        SendProviderAgreesToCareFamilyConfirmationEmail sendProviderAgreesToCareFamilyConfirmationEmail,
        SendProviderDeclinesCareFamilyConfirmationEmail sendProviderDeclinesCareFamilyConfirmationEmail,
        SubmissionRepositoryService submissionRepositoryService) {
        this.sendProviderRespondedConfirmationEmail = sendProviderRespondedConfirmationEmail;
        this.sendProviderAgreesToCareFamilyConfirmationEmail = sendProviderAgreesToCareFamilyConfirmationEmail;
        this.sendProviderDeclinesCareFamilyConfirmationEmail = sendProviderDeclinesCareFamilyConfirmationEmail;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission providerSubmission) {
        // If a provider is an existing provider that has done CCAP stuff before, send emails
        // New Provider Registration will send the emails later
        // if the providerSubmission has expired then we should not send the emails linked to this action

        if (!ProviderSubmissionUtilities.isProviderRegistering(providerSubmission) && ProviderSubmissionUtilities.providerSubmissionHasNotExpired(providerSubmission, submissionRepositoryService)) {
            sendProviderAgreesToCareFamilyConfirmationEmail.send(providerSubmission);
            sendProviderDeclinesCareFamilyConfirmationEmail.send(providerSubmission);
            sendProviderRespondedConfirmationEmail.send(providerSubmission);
        }
    }
}
