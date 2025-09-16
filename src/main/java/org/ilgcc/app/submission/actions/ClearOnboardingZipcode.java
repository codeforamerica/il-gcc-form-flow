package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClearOnboardingZipcode implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    private static final String APPLICATION_ZIPCODE_INPUT_NAME = "applicationZipCode";

    @Override
    public void run(Submission submission) {
        submission.getInputData().remove(APPLICATION_ZIPCODE_INPUT_NAME);
        submissionRepositoryService.save(submission);
    }
}