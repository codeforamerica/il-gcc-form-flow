package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_PROVIDER_SUBMISSION_STATUS;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.ilgcc.app.utils.enums.ProviderSubmissionStatus;
import org.springframework.stereotype.Component;

@Component
public class IsConfirmationCodeInactive implements Condition {

    private final HttpSession httpSession;

    public IsConfirmationCodeInactive(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public Boolean run(Submission submission) {
        List<String> inactiveCodes = List.of(ProviderSubmissionStatus.EXPIRED.name(), ProviderSubmissionStatus.RESPONDED.name());
        String providerSubmissionStatus = (String) httpSession.getAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS);
        return inactiveCodes.contains(providerSubmissionStatus);
    }
}
