package org.ilgcc.jobs;


import com.google.common.io.Files;
import formflow.library.data.Submission;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.providerApplicationHasExpired;

import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.ByteArrayMultipartFile;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class TransmissionsRecurringJob {

    private final S3PresignService s3PresignService;
    private final TransmissionRepositoryService transmissionRepositoryService;
    private final UserFileRepositoryService userFileRepositoryService;
    private final UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;
    private final String waitForProviderResponseFlag;
    private final PdfTransmissionJob pdfTransmissionJob;
    private final String CONTENT_TYPE = "application/pdf";

    public TransmissionsRecurringJob(
            S3PresignService s3PresignService,
            TransmissionRepositoryService transmissionRepositoryService,
            UserFileRepositoryService userFileRepositoryService, UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob,
            PdfService pdfService,
            CloudFileRepository cloudFileRepository, PdfTransmissionJob pdfTransmissionJob,
            @Value("${il-gcc.dts.wait-for-provider-response}") String waitForProviderResponseFlag) {
        this.s3PresignService = s3PresignService;
        this.transmissionRepositoryService = transmissionRepositoryService;
        this.userFileRepositoryService = userFileRepositoryService;
        this.uploadedDocumentTransmissionJob = uploadedDocumentTransmissionJob;
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.waitForProviderResponseFlag = waitForProviderResponseFlag;
    }

    @Recurring(id = "no-provider-response-job", cron = "* * * * *")
    @Job(name = "No provider response job")
    public void NoProviderResponseJob() {
        List<Submission> submissionsWithoutTransmissions = transmissionRepositoryService.findSubmissionsWithoutTransmission();
        LocalDate todaysDate = LocalDate.now();

        if (waitForProviderResponseFlag.equals("false")) {
            return;
        } else if (submissionsWithoutTransmissions.isEmpty()) {
            return;
        } else {
            for (Submission s : submissionsWithoutTransmissions) {
                if (!hasProviderResponse(s) && providerApplicationHasExpired(s, todaysDate)) {
                    uploadPdfSubmissionToS3(s);
                    sendUploadedDocumentsToDocumentTransferService(s);
                } else if (hasProviderResponse(s) && providerApplicationHasExpired(s, todaysDate)) {
                    throw new IllegalStateException(String.format(
                            "Weird, provider response exist but the provider response expired. Check submission: %s", s.getId()));
                }
            }
        }
    }

    private boolean hasProviderResponse(Submission submission) {
        return submission.getInputData().containsKey("providerResponseSubmissionId");
    }

    private void uploadPdfSubmissionToS3(Submission submission) {
        try {
            byte[] pdfFile = pdfService.getFilledOutPDF(submission);
            String pdfFileName = String.format(FileNameUtility.getFileNameForPdf(submission));
            MultipartFile multipartFile = new ByteArrayMultipartFile(pdfFile, pdfFileName, CONTENT_TYPE);
            String s3ZipPath = SubmissionUtilities.generatePdfPath(submission);

            CompletableFuture.runAsync(() -> {
                try {
                    cloudFileRepository.upload(s3ZipPath, multipartFile);
                } catch (IOException | InterruptedException e) {
                    log.error("Error uploading submission to S3", e);
                    throw new RuntimeException(e);
                }
            }).thenRun(() -> {
                try {
                    pdfTransmissionJob.enqueuePdfTransmissionJob(s3ZipPath, submission);
                } catch (IOException e) {
                    log.error("An error occurred when enqueuing a job with the document transfer service.", e);
                }
            }).exceptionally(e -> {
                log.error("Error uploading submission to S3 for submission with ID: {}", submission.getId(), e);
                return null;
            });

        } catch (IOException e) {
            log.error("Error uploading submission to S3 for submission with ID: {}", submission.getId(), e);
        }
    }

    private void sendUploadedDocumentsToDocumentTransferService(Submission submission) {
        log.info("Sending uploaded files to document transfer service for submission with ID: {}", submission.getId());
        List<UserFile> userFiles = userFileRepositoryService.findAllBySubmission(submission);
        if (!userFiles.isEmpty()) {
            for (int i = 0; i < userFiles.size(); i++) {
                UserFile userFile = userFiles.get(i);
                String fileExtension = Files.getFileExtension(userFile.getOriginalName());
                String fileName = FileNameUtility.getFileNameForUploadedDocument(submission, i + 1, userFiles.size(),
                        fileExtension);
                CompletableFuture<Boolean> scannedAndCleanFuture = s3PresignService.isObjectScannedAndClean(
                        userFile.getRepositoryPath());
                int currentFileIndex = i;
                scannedAndCleanFuture.thenAccept(scannedAndClean -> {
                    if (scannedAndClean) {
                        log.info("Sending file {} of {} to document transfer service for submission with ID: {}",
                                currentFileIndex + 1, userFiles.size(), submission.getId());
                        uploadedDocumentTransmissionJob.enqueueUploadedDocumentTransmissionJob(submission, userFile, fileName);
                    }
                }).exceptionally(e -> {
                    log.error(
                            "There was an error when attempting to send uploaded file with id: {} in submission with id: {} to the document transfer service. It's possible the file had a virus, or could not be scanned.",
                            userFile.getFileId(), submission.getId(), e);
                    return null;
                });
            }
        }
    }
}
