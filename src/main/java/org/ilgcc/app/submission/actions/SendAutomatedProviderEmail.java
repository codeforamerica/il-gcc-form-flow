package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        if ("true".equals(familySubmission.getInputData().get("hasConfirmedIntendedProviderEmail"))) {
            sendAutomatedProviderOutreachEmail.send(familySubmission);
        }
    }

    @Override
    public void run(Submission familySubmission, String contactProvidersSubflowUUID) {
        Optional<Map<String, Object>> subflow = Optional.of(familySubmission.getSubflowEntryByUuid("contactProviders", contactProvidersSubflowUUID));
        if ("true".equals(subflow.get().get("hasConfirmedIntendedProviderEmail"))) {
            sendAutomatedProviderOutreachEmail.send(familySubmission);
        }
    }
}
