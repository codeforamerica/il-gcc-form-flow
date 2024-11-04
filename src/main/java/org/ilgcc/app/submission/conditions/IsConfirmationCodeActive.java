package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.submission.actions.CheckClientSubmissionForProvider.SESSION_KEY_CLIENT_SUBMISSION_STATUS;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import jakarta.servlet.http.HttpSession;
import org.ilgcc.app.utils.enums.ProviderSubmissionStatus;
import org.springframework.stereotype.Component;

@Component
public class IsConfirmationCodeActive implements Condition {

    private final HttpSession httpSession;

    public IsConfirmationCodeActive(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public Boolean run(Submission submission) {
        String providerSubmissionStatus = (String) httpSession.getAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS);
        return providerSubmissionStatus != null && ProviderSubmissionStatus.ACTIVE.equals(ProviderSubmissionStatus.valueOf(providerSubmissionStatus));
    }
}
