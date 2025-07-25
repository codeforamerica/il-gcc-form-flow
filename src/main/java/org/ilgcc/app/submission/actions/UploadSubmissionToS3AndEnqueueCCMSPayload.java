package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.utils.SubmissionUtilities.isNoProviderSubmission;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.SubmissionSenderService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UploadSubmissionToS3AndEnqueueCCMSPayload implements Action {

    private final SubmissionSenderService submissionSenderService;

    // TODO: Rename this class to just "SendSubmissionToCCMS" once mega PR is approved. -Marc
    public UploadSubmissionToS3AndEnqueueCCMSPayload(SubmissionSenderService submissionSenderService) {
        this.submissionSenderService = submissionSenderService;
    }

    @Override
    public void run(Submission submission) {
        if (isNoProviderSubmission(submission.getInputData())) {
            submissionSenderService.sendFamilySubmission(submission);
        }
    }
}