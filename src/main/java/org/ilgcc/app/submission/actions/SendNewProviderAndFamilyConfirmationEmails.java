package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendNewProviderAgreesToCareFamilyConfirmationEmail;
import org.ilgcc.app.email.SendNewProviderConfirmationEmail;
import org.ilgcc.app.email.SendProviderAgreesToCareFamilyConfirmationEmail;
import org.ilgcc.app.email.SendProviderConfirmationEmail;
import org.ilgcc.app.email.SendProviderDeclinesCareFamilyConfirmationEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendNewProviderAndFamilyConfirmationEmails implements Action {

    @Autowired
    SendNewProviderAgreesToCareFamilyConfirmationEmail sendNewProviderAgreesToCareFamilyConfirmationEmail;

    @Autowired
    SendNewProviderConfirmationEmail sendNewProviderConfirmationEmail;

    @Override
    public void run(Submission submission) {
        sendNewProviderAgreesToCareFamilyConfirmationEmail.send(submission);
        sendNewProviderConfirmationEmail.send(submission);
    }
}
