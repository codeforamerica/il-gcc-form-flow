package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.FileNameUtility;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.PdfTransmissionJob;
import org.ilgcc.jobs.UploadedDocumentTransmissionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class UploadProviderSubmissionToS3AndSendToCCMS implements Action {

    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;
    private final PdfTransmissionJob pdfTransmissionJob;
    private final EnqueueDocumentTransfer enqueueDocumentTransfer;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final UserFileRepositoryService userFileRepositoryService;
    private final UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
    private final S3PresignService s3PresignService;
    private final CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob;
    private boolean CCMMS_INTEGRATION_ENABLED;
    private boolean DTS_INTEGRATION_ENABLED;

    public UploadProviderSubmissionToS3AndSendToCCMS(PdfService pdfService,
            CloudFileRepository cloudFileRepository,
            PdfTransmissionJob pdfTransmissionJob,
            EnqueueDocumentTransfer enqueueDocumentTransfer,
            SubmissionRepositoryService submissionRepositoryService,
            UserFileRepositoryService userFileRepositoryService,
            UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob,
            S3PresignService s3PresignService,
            CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob,
            @Value("${il-gcc.ccms-integration-enabled:false}") boolean ccmsIntegrationEnabled,
            @Value("${il-gcc.dts-integration-enabled}") boolean dtsIntegrationEnabled) {
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.enqueueDocumentTransfer = enqueueDocumentTransfer;
        this.submissionRepositoryService = submissionRepositoryService;
        this.userFileRepositoryService = userFileRepositoryService;
        this.uploadedDocumentTransmissionJob = uploadedDocumentTransmissionJob;
        this.s3PresignService = s3PresignService;
        this.ccmsSubmissionPayloadTransactionJob = ccmsSubmissionPayloadTransactionJob;
        CCMMS_INTEGRATION_ENABLED = ccmsIntegrationEnabled;
        DTS_INTEGRATION_ENABLED = dtsIntegrationEnabled;
    }

    @Override
    public void run(Submission providerSubmission) {
        var familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId.get());
            if (familySubmissionOptional.isPresent()) {
                log.info("Provider submitted response for family submission {}, enqueuing transfer of documents.",
                        familySubmissionId.get());
                Submission familySubmission = familySubmissionOptional.get();
                familySubmission.getInputData().put("providerResponseSubmissionId", providerSubmission.getId().toString());
                submissionRepositoryService.save(familySubmission);
                if (DTS_INTEGRATION_ENABLED) {
                    enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository, pdfTransmissionJob,
                            familySubmission, FileNameUtility.getFileNameForPdf(familySubmission, "Provider-Responded"));
                    enqueueDocumentTransfer.enqueueUploadedDocumentBySubmission(userFileRepositoryService,
                            uploadedDocumentTransmissionJob, s3PresignService, familySubmission);
                }
                if (CCMMS_INTEGRATION_ENABLED) {
                    ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadWithDelay(familySubmission);
                }
            } else {
                log.error(String.format("We can not find a match for your family submission: %s", familySubmissionId.get()));
            }
        } else {
            log.error(String.format("Family Submission Id is Blank for the provider submission: %s.",
                    providerSubmission.getId().toString()));
        }
    }
}