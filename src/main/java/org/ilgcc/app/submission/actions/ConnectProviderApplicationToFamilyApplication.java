package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_SUBMISSION_ID;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import jakarta.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConnectProviderApplicationToFamilyApplication implements Action {

    private final HttpSession httpSession;


    public ConnectProviderApplicationToFamilyApplication(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public void run(Submission providerSubmission) {
        UUID familySubmissionId = (UUID) httpSession.getAttribute(SESSION_KEY_FAMILY_SUBMISSION_ID);

        if (familySubmissionId != null && !familySubmissionId.toString().isEmpty()) {
            providerSubmission.getInputData().put("familySubmissionId", familySubmissionId);
        }

        String providerNumber = (String) providerSubmission.getInputData().getOrDefault("providerResponseProviderNumber", "");
        if (!providerNumber.isEmpty()) {
            providerSubmission.getInputData().put("providerResponseProviderNumber", new BigInteger(providerNumber));
        }
    }
}
