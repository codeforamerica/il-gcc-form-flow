package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.utils.SubmissionUtilities.isNoProviderSubmission;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.SubmissionSenderService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UploadSubmissionToS3AndEnqueueCCMSPayload implements Action {

    private final SubmissionSenderService submissionSenderService;
    private final SubmissionRepositoryService submissionRepositoryService;

    public UploadSubmissionToS3AndEnqueueCCMSPayload(SubmissionSenderService submissionSenderService,
            SubmissionRepositoryService submissionRepositoryService) {
        this.submissionSenderService = submissionSenderService;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission submission) {
        if (isNoProviderSubmission(submission.getInputData())) {
            submissionSenderService.sendFamilySubmission(submission);
        }
    }
}