package org.ilgcc.app.submission.actions;

import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendProviderAgreesToCareFamilyConfirmationEmail;
import org.ilgcc.app.email.SendProviderConfirmationEmail;
import org.ilgcc.app.email.SendProviderDeclinesCareFamilyConfirmationEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import formflow.library.config.submission.Action;
@Component
@Slf4j
public class SendProviderAndFamilyEmails implements Action {

    @Autowired
    SendProviderAgreesToCareFamilyConfirmationEmail sendProviderAgreesToCareFamilyConfirmationEmail;

    @Autowired
    SendProviderConfirmationEmail sendProviderConfirmationEmail;

    @Autowired
    SendProviderDeclinesCareFamilyConfirmationEmail sendProviderDeclinesCareFamilyConfirmationEmail;

    @Override
    public void run(Submission submission) {
        sendProviderAgreesToCareFamilyConfirmationEmail.send();
        sendProviderConfirmationEmail.send();
        sendProviderDeclinesCareFamilyConfirmationEmail.send();
    }
}
