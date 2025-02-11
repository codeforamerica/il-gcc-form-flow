package org.ilgcc.jobs;

import com.google.common.io.Files;
import formflow.library.data.Submission;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.ByteArrayMultipartFile;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class EnqueueDocumentTransfer {

    @Value("${form-flow.uploads.file-conversion.convert-to-pdf:false}")
    private boolean convertUploadToPDF;

    private final static String CONTENT_TYPE = "application/pdf";

    public void enqueuePDFDocumentBySubmission(PdfService pdfService, CloudFileRepository cloudFileRepository,
            PdfTransmissionJob pdfTransmissionJob, Submission submission, String fileNameForPdf) {
        try {
            byte[] pdfFile = pdfService.getFilledOutPDF(submission);
            String pdfFileName = String.format(fileNameForPdf);
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
                    pdfTransmissionJob.enqueuePdfTransmissionJob(s3ZipPath, submission, pdfFileName);
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

    public void enqueueUploadedDocumentBySubmission(UserFileRepositoryService userFileRepositoryService,
            UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob, S3PresignService s3PresignService,
            Submission submission) {
        log.info("Sending uploaded files to document transfer service for submission with ID: {}", submission.getId());
        List<UserFile> userFiles;
        if (convertUploadToPDF) {
            log.info("Finding all uploaded and converted files of type {}", CONTENT_TYPE);
            userFiles = userFileRepositoryService.findAllOrderByOriginalName(submission, CONTENT_TYPE);
        } else {
            userFiles = userFileRepositoryService.findAllOrderByOriginalName(submission);
        }

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

