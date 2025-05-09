package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendAutomatedProviderOutreachEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendAutomatedProviderEmail implements Action {

    @Autowired
    SendAutomatedProviderOutreachEmail sendAutomatedProviderOutreachEmail;

    @Override
    public void run(Submission familySubmission) {
        if (familySubmission.getInputData().get("hasConfirmedIntendedProviderEmail").equals("true")) {
            sendAutomatedProviderOutreachEmail.send(familySubmission);
        }
    }
}
