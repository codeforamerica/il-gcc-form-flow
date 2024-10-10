package org.ilgcc.jobs;

import formflow.library.data.Submission;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ByteArrayMultipartFile;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class EnqueuePdfDocumentTransfer {

    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;
    private final PdfTransmissionJob pdfTransmissionJob;
    private final String CONTENT_TYPE = "application/pdf";

    public EnqueuePdfDocumentTransfer(PdfService pdfService, CloudFileRepository cloudFileRepository,
            PdfTransmissionJob pdfTransmissionJob){
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
    }

    public void enqueueBySubmission(Submission submission, String fileNameForPdf) {
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
}

