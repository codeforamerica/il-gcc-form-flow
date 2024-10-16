package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ByteArrayMultipartFile;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.ilgcc.jobs.PdfTransmissionJob;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
public class UploadSubmissionToS3 implements Action {

    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;

    private final PdfTransmissionJob pdfTransmissionJob;
    private final String CONTENT_TYPE = "application/pdf";

    private final String enableBackgroundJobs;

    private final String waitForProviderResponseFlag;

    public UploadSubmissionToS3(PdfService pdfService, CloudFileRepository cloudFileRepository,
            PdfTransmissionJob pdfTransmissionJob,
            @Value("${il-gcc.dts.enable-background-jobs}") String enableBackgroundJobs,
            @Value("${il-gcc.dts.wait-for-provider-response}") String waitForProviderResponseFlag) {
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.enableBackgroundJobs = enableBackgroundJobs;
        this.waitForProviderResponseFlag = waitForProviderResponseFlag;
    }

    @Override
    public void run(Submission submission) {

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
                    if (enableBackgroundJobs.equals("true") && waitForProviderResponseFlag.equals("false")) {
                        pdfTransmissionJob.enqueuePdfTransmissionJob(s3ZipPath, submission);
                    }
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