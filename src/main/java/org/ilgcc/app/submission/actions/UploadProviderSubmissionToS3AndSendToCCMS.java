package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.SubmissionSenderService;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class UploadProviderSubmissionToS3AndSendToCCMS implements Action {

    private final SubmissionSenderService submissionSenderService;

    public UploadProviderSubmissionToS3AndSendToCCMS(SubmissionSenderService submissionSenderService) {
        this.submissionSenderService = submissionSenderService;
    }

    @Override
    public void run(Submission providerSubmission) {
        submissionSenderService.sendProviderSubmission(providerSubmission);
    }
}