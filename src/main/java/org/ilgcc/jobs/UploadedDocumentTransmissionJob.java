package org.ilgcc.jobs;

import static org.ilgcc.app.utils.enums.TransmissionStatus.Queued;
import static org.ilgcc.app.utils.enums.TransmissionType.UPLOADED_DOCUMENT;
import static org.jobrunr.scheduling.JobBuilder.aJob;
import formflow.library.data.Submission;
import formflow.library.data.UserFile;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.Transmission;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.file_transfer.DocumentTransferRequestService;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.jobrunr.scheduling.BackgroundJob;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UploadedDocumentTransmissionJob {

    private final S3PresignService s3PresignService;
    private final DocumentTransferRequestService documentTransferRequestService;
    private final TransmissionRepositoryService transmissionRepositoryService;

    public UploadedDocumentTransmissionJob(
            S3PresignService s3PresignService,
            DocumentTransferRequestService documentTransferRequestService,
            TransmissionRepositoryService transmissionRepositoryService) {
        this.s3PresignService = s3PresignService;
        this.documentTransferRequestService = documentTransferRequestService;
        this.transmissionRepositoryService = transmissionRepositoryService;
    }
    
    public void enqueueUploadedDocumentTransmissionJob(Submission submission, UserFile userFile, String fileName) {
        Date now = Date.from(ZonedDateTime.now(ZoneId.of("America/Chicago")).toInstant());
        Transmission uploadedDocumentTransmission = transmissionRepositoryService.save(new Transmission(submission, userFile, now, Queued, UPLOADED_DOCUMENT, null));
        UUID uploadedDocumentTransmissionId = uploadedDocumentTransmission.getTransmissionId();
        BackgroundJob.create(aJob()
                .withName("Send Document Transfer Request for Uploaded Document")
                .withAmountOfRetries(3)
                .scheduleIn(Duration.ofSeconds(5))
                .<UploadedDocumentTransmissionJob>withDetails(x -> x.sendUploadedDocumentTransferRequest(submission, userFile, fileName, uploadedDocumentTransmissionId)));
    }
    
    public void sendUploadedDocumentTransferRequest(Submission submission, UserFile userFile, String fileName, UUID uploadedDocumentTransmissionId)
            throws IOException, URISyntaxException {
        String presignedUrl = s3PresignService.generatePresignedUrl(userFile.getRepositoryPath());
        log.info("Enqueuing uploaded document transfer job for file with ID: {} in submission with ID: {}", userFile.getFileId(), submission.getId());
        documentTransferRequestService.sendDocumentTransferServiceRequest(presignedUrl, submission, fileName, uploadedDocumentTransmissionId);
    }
}
