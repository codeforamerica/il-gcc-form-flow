package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class SetCCRRName implements Action {

    @Override
    public void run(FormSubmission formSubmission, Submission submission) {
        // Once we implement other CCRR this should be updated based on the county name
        formSubmission.getFormData().put("ccrrName", "4-C: Community Coordinated Child Care");
    }
}

