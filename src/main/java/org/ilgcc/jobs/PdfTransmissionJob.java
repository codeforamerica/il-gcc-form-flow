package org.ilgcc.jobs;

import formflow.library.data.Submission;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.file_transfer.DocumentTransferRequestService;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.enums.FileNameUtility;
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

    public PdfTransmissionJob(
            S3PresignService s3PresignService, 
            JobScheduler jobScheduler, DocumentTransferRequestService documentTransferRequestService) {
        this.s3PresignService = s3PresignService;
        this.jobScheduler = jobScheduler;
        this.documentTransferRequestService = documentTransferRequestService;
    }

    public void enqueuePdfTransmissionJob(String objectPath, Submission submission) throws IOException {
        String presignedUrl = s3PresignService.generatePresignedUrl(objectPath);
        String fileNameForPdf = FileNameUtility.getFileNameForPdf(submission);
        JobId jobId = jobScheduler.enqueue(() -> sendPdfTransferRequest(presignedUrl, submission, fileNameForPdf));
        log.info("Enqueued job with ID: {} for submission with ID: {}", jobId, submission.getId());
    }
    
    @Job(name = "Send Document Transfer Service Request", retries = 5)
    public void sendPdfTransferRequest(String presignedUrl, Submission submission, String fileName) throws IOException {
        documentTransferRequestService.sendDocumentTransferServiceRequest(presignedUrl, submission, fileName);
    }
}
