package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SaveApplicationId implements Action {

    private final HttpSession httpSession;


    public SaveApplicationId(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public void run(Submission providerSubmission) {
        UUID clientSubmissionId = (UUID) httpSession.getAttribute("clientSubmissionId");

        if (clientSubmissionId != null && !clientSubmissionId.toString().isEmpty()) {
            providerSubmission.getInputData().put("familySubmissionId", clientSubmissionId);
        }
    }
}
