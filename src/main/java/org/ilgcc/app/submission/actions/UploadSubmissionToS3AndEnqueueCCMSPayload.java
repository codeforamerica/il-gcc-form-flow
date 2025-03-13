package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.utils.SubmissionUtilities.hasNotChosenProvider;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.FileNameUtility;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.PdfTransmissionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UploadSubmissionToS3AndEnqueueCCMSPayload implements Action {

    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;
    private final PdfTransmissionJob pdfTransmissionJob;
    private final CCMSSubmissionPayloadTransactionJob CCMSSubmissionPayloadTransactionJob;
    private final EnqueueDocumentTransfer enqueueDocumentTransfer;
    private final boolean CCMS_INTEGRATION_ENABLED;
    private final boolean DTS_INTEGRATION_ENABLED;

    public UploadSubmissionToS3AndEnqueueCCMSPayload(PdfService pdfService, CloudFileRepository cloudFileRepository,
            PdfTransmissionJob pdfTransmissionJob,
            CCMSSubmissionPayloadTransactionJob CCMSSubmissionPayloadTransactionJob,
            EnqueueDocumentTransfer enqueueDocumentTransfer,
            @Value("${il-gcc.ccms-integration-enabled:false}") boolean ccmsIntegrationEnabled,
            @Value("${il-gcc.dts-integration-enabled}") boolean dtsIntegrationEnabled) {
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.CCMSSubmissionPayloadTransactionJob = CCMSSubmissionPayloadTransactionJob;
        this.enqueueDocumentTransfer = enqueueDocumentTransfer;
        CCMS_INTEGRATION_ENABLED = ccmsIntegrationEnabled;
        DTS_INTEGRATION_ENABLED = dtsIntegrationEnabled;
    }

    @Override
    public void run(Submission submission) {
        if (hasNotChosenProvider(submission)) {
            if (DTS_INTEGRATION_ENABLED) {
                enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository, pdfTransmissionJob,
                        submission, FileNameUtility.getFileNameForPdf(submission, "Form-Family"));
            }

            if (CCMS_INTEGRATION_ENABLED) {
                CCMSSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadWithDelay(submission);
            }
        }
    }
}