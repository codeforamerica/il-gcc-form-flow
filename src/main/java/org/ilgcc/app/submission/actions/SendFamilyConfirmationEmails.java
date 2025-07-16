package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendFamilyConfirmationEmail;
import org.ilgcc.app.email.SendFamilyConfirmationNoProviderEmail;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendFamilyConfirmationEmails implements Action {

    @Autowired
    SendFamilyConfirmationNoProviderEmail sendNoProviderConfirmationEmail;

    @Autowired
    SendFamilyConfirmationEmail sendFamilyConfirmationEmail;

    @Override
    public void run(Submission familySubmission) {
        if (SubmissionUtilities.isNoProviderSubmission(familySubmission.getInputData())) {
            sendNoProviderConfirmationEmail.send(familySubmission);
        } else {
            sendFamilyConfirmationEmail.send(familySubmission);
        }
    }
}
