package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendProviderRespondedConfirmationEmail;
import org.ilgcc.app.email.SendNewProviderAgreesToCareFamilyConfirmationEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendNewProviderAndFamilyConfirmationEmails implements Action {

    @Autowired
    SendNewProviderAgreesToCareFamilyConfirmationEmail sendNewProviderAgreesToCareFamilyConfirmationEmail;

    @Autowired
    SendProviderRespondedConfirmationEmail sendProviderRespondedConfirmationEmail;

    @Override
    public void run(Submission submission) {
        sendNewProviderAgreesToCareFamilyConfirmationEmail.send(submission);
        sendProviderRespondedConfirmationEmail.send(submission);
    }
}
