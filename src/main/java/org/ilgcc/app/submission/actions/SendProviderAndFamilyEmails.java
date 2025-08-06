package org.ilgcc.app.submission.actions;

import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendProviderAgreesToCareFamilyConfirmationEmail;
import org.ilgcc.app.email.SendProviderConfirmationAfterResponseEmail;
import org.ilgcc.app.email.SendProviderDeclinesCareFamilyConfirmationEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import formflow.library.config.submission.Action;
@Component
@Slf4j
public class SendProviderAndFamilyEmails implements Action {

    @Autowired
    SendProviderConfirmationAfterResponseEmail sendProviderConfirmationAfterResponseEmail;

    @Autowired
    SendProviderAgreesToCareFamilyConfirmationEmail sendProviderAgreesToCareFamilyConfirmationEmail;

    @Autowired
    SendProviderDeclinesCareFamilyConfirmationEmail sendProviderDeclinesCareFamilyConfirmationEmail;

    @Override
    public void run(Submission submission) {
        // If a provider is an existing provider that has done CCAP stuff before, send emails
        // New Provider Registration will send the emails later
        if (!ProviderSubmissionUtilities.isProviderRegistering(submission)) {
            sendProviderAgreesToCareFamilyConfirmationEmail.send(submission);
            sendProviderDeclinesCareFamilyConfirmationEmail.send(submission);
        }
       sendProviderConfirmationAfterResponseEmail.send(submission);
    }
}
