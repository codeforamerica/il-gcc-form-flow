package org.ilgcc.jobs;

import static org.ilgcc.app.utils.enums.TransmissionStatus.Queued;
import static org.ilgcc.app.utils.enums.TransmissionType.APPLICATION_PDF;
import formflow.library.data.Submission;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.Transmission;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.file_transfer.DocumentTransferRequestService;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.ilgcc.app.utils.enums.TransmissionStatus;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PdfTransmissionJob {
    
    private final S3PresignService s3PresignService;
    private final JobScheduler jobScheduler;
    private final DocumentTransferRequestService documentTransferRequestService;
    private final TransmissionRepositoryService transmissionRepositoryService;

    public PdfTransmissionJob(
            S3PresignService s3PresignService, 
            JobScheduler jobScheduler, 
            DocumentTransferRequestService documentTransferRequestService,
            TransmissionRepositoryService transmissionRepositoryService) {
        this.s3PresignService = s3PresignService;
        this.jobScheduler = jobScheduler;
        this.documentTransferRequestService = documentTransferRequestService;
        this.transmissionRepositoryService = transmissionRepositoryService;
    }

    public void enqueuePdfTransmissionJob(String objectPath, Submission submission, String pdfFileName) throws IOException {
        String presignedUrl = s3PresignService.generatePresignedUrl(objectPath);
        Date now = Date.from(ZonedDateTime.now(ZoneId.of("America/Chicago")).toInstant());
        Transmission pdfTransmission = transmissionRepositoryService.save(new Transmission(submission, null, now, Queued, APPLICATION_PDF, null));
        UUID transmissionId = pdfTransmission.getTransmissionId();
        JobId jobId = jobScheduler.enqueue(() -> sendPdfTransferRequest(presignedUrl, submission, pdfFileName, transmissionId));
        log.info("Enqueued job with ID: {} for submission with ID: {}", jobId, submission.getId());
    }

    @Job(name = "Send Document Transfer Service Request for Application PDF", retries = 5)
    public void sendPdfTransferRequest(String presignedUrl, Submission submission, String fileName, UUID transmissionId) throws IOException {
        documentTransferRequestService.sendDocumentTransferServiceRequest(presignedUrl, submission, fileName, transmissionId);
    }
}
