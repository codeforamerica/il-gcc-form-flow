package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ByteArrayMultipartFile;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.jobs.PdfTransmissionJobService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
public class UploadSubmissionToS3 implements Action {

    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;

    private final PdfTransmissionJobService pdfTransmissionJobService;
    private final String CONTENT_TYPE = "application/pdf";
    
    private final String jobRunrEnabled;


    public UploadSubmissionToS3(PdfService pdfService, CloudFileRepository cloudFileRepository,
            PdfTransmissionJobService pdfTransmissionJobService,
            @Value("${org.jobrunr.job-scheduler.enabled}") String jobRunrEnabled) {
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJobService = pdfTransmissionJobService;
        this.jobRunrEnabled = jobRunrEnabled;
    }

    @Override
    public void run(Submission submission) {

        try {
            byte[] pdfFile = pdfService.getFilledOutPDF(submission);
            String pdfFileName = String.format("%s.pdf", submission.getId());
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
                    if (jobRunrEnabled.equals("true")) {
                        pdfTransmissionJobService.enqueuePdfTransmissionJob(s3ZipPath, submission);
                    }
                } catch (IOException e) {
                    log.error("An error occurred when enqueuing a job with the document transfer service.", e);
                }
            });

        } catch (IOException e) {
            log.error("Error uploading submission to S3 for submission with ID: {}", submission.getId(), e);
        }
    }
}