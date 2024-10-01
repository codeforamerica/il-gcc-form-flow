package org.ilgcc.jobs;

import static org.ilgcc.app.utils.enums.TransmissionType.UPLOADED_DOCUMENT;
import static org.jobrunr.scheduling.JobBuilder.aJob;
import formflow.library.data.Submission;
import formflow.library.data.UserFile;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.Transmission;
import org.ilgcc.app.file_transfer.DocumentTransferRequestService;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.jobrunr.scheduling.BackgroundJob;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UploadedDocumentTransmissionJob {

    private final S3PresignService s3PresignService;
    private final DocumentTransferRequestService documentTransferRequestService;

    public UploadedDocumentTransmissionJob(
            S3PresignService s3PresignService,
            DocumentTransferRequestService documentTransferRequestService) {
        this.s3PresignService = s3PresignService;
        this.documentTransferRequestService = documentTransferRequestService;
    }
    
    public void enqueueUploadedDocumentTransmissionJob(Submission submission, UserFile userFile, String fileName) {
        BackgroundJob.create(aJob()
                .withName("Send Document Transfer Request for Uploaded Document")
                .withAmountOfRetries(3)
                .scheduleIn(Duration.ofSeconds(5))
                .<UploadedDocumentTransmissionJob>withDetails(x -> x.sendUploadedDocumentTransferRequest(submission, userFile, fileName)));
    }
    
    public void sendUploadedDocumentTransferRequest(Submission submission, UserFile userFile, String fileName)
            throws IOException {
        String presignedUrl = s3PresignService.generatePresignedUrl(userFile.getRepositoryPath());
        log.info("Enqueuing uploaded document transfer job for file with ID: {} in submission with ID: {}", userFile.getFileId(), submission.getId());
        Date now = Date.from(ZonedDateTime.now(ZoneId.of("America/Chicago")).toInstant());
        Transmission uploadedDocumentTransmission =
                new Transmission(submission, userFile, now, null, UPLOADED_DOCUMENT, null);
        documentTransferRequestService.sendDocumentTransferServiceRequest(presignedUrl, submission, fileName, uploadedDocumentTransmission);
    }
}
