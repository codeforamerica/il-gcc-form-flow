package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_CAME_FROM_HOME_PAGE;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_CONFIRMATION_CODE;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class ShouldSkipConfirmationCode implements Condition {

    private final HttpSession httpSession;
    private final SubmissionRepositoryService submissionRepositoryService;

    public ShouldSkipConfirmationCode(HttpSession httpSession, SubmissionRepositoryService submissionRepositoryService) {
        this.httpSession = httpSession;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public Boolean run(Submission submission) {
        Boolean cameFromHomePage = (Boolean) httpSession.getAttribute(SESSION_KEY_CAME_FROM_HOME_PAGE);
        boolean shouldSkip = cameFromHomePage != null && cameFromHomePage;

        if (shouldSkip) {
            String confirmationCode = (String) httpSession.getAttribute(SESSION_KEY_FAMILY_CONFIRMATION_CODE);
            confirmationCode = confirmationCode != null ? confirmationCode.toUpperCase() : null;
            submission.getInputData().put("providerResponseFamilyShortCode", confirmationCode);
            submissionRepositoryService.save(submission);
        }

        return shouldSkip;
    }
}
