package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_CAME_FROM_HOME_PAGE;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class ShouldSkipConfirmationCode implements Condition {

    private final HttpSession httpSession;

    public ShouldSkipConfirmationCode(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public Boolean run(Submission submission) {
        Boolean cameFromHomePage = (Boolean) httpSession.getAttribute(SESSION_KEY_CAME_FROM_HOME_PAGE);
        return cameFromHomePage != null && cameFromHomePage;
    }
}
