package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.utils.SubmissionUtilities.hasNotChosenProvider;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.FileNameUtility;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.PdfTransmissionJob;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UploadSubmissionToS3 implements Action {

    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;

    private final PdfTransmissionJob pdfTransmissionJob;

    private final EnqueueDocumentTransfer enqueueDocumentTransfer;

    public UploadSubmissionToS3(PdfService pdfService, CloudFileRepository cloudFileRepository,
            PdfTransmissionJob pdfTransmissionJob,
            EnqueueDocumentTransfer enqueueDocumentTransfer) {
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.enqueueDocumentTransfer = enqueueDocumentTransfer;
    }

    @Override
    public void run(Submission submission) {
        if (hasNotChosenProvider(submission)) {
            enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository, pdfTransmissionJob,
                    submission, FileNameUtility.getFileNameForPdf(submission, "Form-Family"));
        }
    }
}