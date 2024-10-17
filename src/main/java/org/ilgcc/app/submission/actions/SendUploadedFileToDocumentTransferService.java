package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.UserFileRepositoryService;

import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.UploadedDocumentTransmissionJob;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendUploadedFileToDocumentTransferService implements Action {

    private final UserFileRepositoryService userFileRepositoryService;
    private final UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
    private final S3PresignService s3PresignService;
    private final String waitForProviderResponseFlag;

    private final EnqueueDocumentTransfer enqueueDocumentTransfer;

    public SendUploadedFileToDocumentTransferService(UserFileRepositoryService userFileRepositoryService,
            UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob, S3PresignService s3PresignService, EnqueueDocumentTransfer enqueueDocumentTransfer,
            @Value("${il-gcc.dts.wait-for-provider-response}") String waitForProviderResponseFlag) {
        this.userFileRepositoryService = userFileRepositoryService;
        this.uploadedDocumentTransmissionJob = uploadedDocumentTransmissionJob;
        this.s3PresignService = s3PresignService;
        this.enqueueDocumentTransfer = enqueueDocumentTransfer;
        this.waitForProviderResponseFlag=waitForProviderResponseFlag;
    }

    @Override
    public void run(Submission submission) {
        if (waitForProviderResponseFlag.equals("false")) {
           enqueueDocumentTransfer.enqueueUploadedDocumentBySubmission(userFileRepositoryService, uploadedDocumentTransmissionJob, s3PresignService, submission);
        }
    }
}

