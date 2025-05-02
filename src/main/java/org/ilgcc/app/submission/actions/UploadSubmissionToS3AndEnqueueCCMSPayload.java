package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.utils.SubmissionUtilities.hasNotChosenProvider;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.SubmissionSenderService;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import org.ilgcc.app.utils.FileNameUtility;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.PdfTransmissionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UploadSubmissionToS3AndEnqueueCCMSPayload implements Action {

    private final SubmissionSenderService submissionSenderService;
    private final SubmissionRepositoryService submissionRepositoryService;

    public UploadSubmissionToS3AndEnqueueCCMSPayload(SubmissionSenderService submissionSenderService, SubmissionRepositoryService submissionRepositoryService) {
        this.submissionSenderService = submissionSenderService;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission submission) {
        if (hasNotChosenProvider(submission)) {
            submissionSenderService.sendFamilySubmission(submission);

            submission.getInputData().put("providerApplicationResponseStatus", SubmissionStatus.INACTIVE.name());
            submissionRepositoryService.save(submission);
        }
    }
}