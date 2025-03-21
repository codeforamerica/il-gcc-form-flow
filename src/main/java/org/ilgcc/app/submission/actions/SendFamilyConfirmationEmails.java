package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendFamilyConfirmationEmail;
import org.ilgcc.app.email.SendFamilyConfirmationNoProviderEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendFamilyConfirmationEmails implements Action {

    @Autowired
    SendFamilyConfirmationNoProviderEmail sendConfirmationEmail;

    @Autowired
    SendFamilyConfirmationEmail sendFamilyConfirmationEmail;

    @Override
    public void run(Submission familySubmission) {
        boolean hasProvider = familySubmission.getInputData().getOrDefault("hasChosenProvider", "false").equals("true");
        if (hasProvider) {
            sendFamilyConfirmationEmail.send();
        } else {
            sendConfirmationEmail.send();
        }

    }
}
