package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.PdfTransmissionJob;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UploadSubmissionToS3 implements Action {

    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;

    private final PdfTransmissionJob pdfTransmissionJob;

    private final EnqueueDocumentTransfer enqueueDocumentTransfer;

    private final String waitForProviderResponseFlag;

    public UploadSubmissionToS3(PdfService pdfService, CloudFileRepository cloudFileRepository,
            PdfTransmissionJob pdfTransmissionJob,
            EnqueueDocumentTransfer enqueueDocumentTransfer,
            @Value("${il-gcc.dts.wait-for-provider-response}") String waitForProviderResponseFlag) {
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.enqueueDocumentTransfer = enqueueDocumentTransfer;
        this.waitForProviderResponseFlag = waitForProviderResponseFlag;
    }

    @Override
    public void run(Submission submission) {
        if (waitForProviderResponseFlag.equals("false")) {
            enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository, pdfTransmissionJob,
                    submission, FileNameUtility.getFileNameForPdf(submission));
        }
    }
}