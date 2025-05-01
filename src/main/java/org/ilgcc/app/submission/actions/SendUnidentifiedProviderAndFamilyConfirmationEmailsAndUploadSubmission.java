package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendProviderDidNotRespondToFamilyEmail;
import org.ilgcc.app.email.SendUnidentifiedProviderConfirmationEmail;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.FileNameUtility;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.PdfTransmissionJob;
import org.ilgcc.jobs.UploadedDocumentTransmissionJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission implements Action {

    @Autowired
    SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;

    @Autowired
    SendUnidentifiedProviderConfirmationEmail sendUnidentifiedProviderConfirmationEmail;

    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;
    private final PdfTransmissionJob pdfTransmissionJob;
    private final EnqueueDocumentTransfer enqueueDocumentTransfer;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final UserFileRepositoryService userFileRepositoryService;
    private final UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
    private final S3PresignService s3PresignService;
    private final CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob;
    private final boolean CCMS_INTEGRATION_ENABLED;
    private final boolean DTS_INTEGRATION_ENABLED;

    public SendUnidentifiedProviderAndFamilyConfirmationEmailsAndUploadSubmission(PdfService pdfService, CloudFileRepository cloudFileRepository,
            PdfTransmissionJob pdfTransmissionJob, EnqueueDocumentTransfer enqueueDocumentTransfer,
            SubmissionRepositoryService submissionRepositoryService, UserFileRepositoryService userFileRepositoryService,
            UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob, S3PresignService s3PresignService,
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
        this.CCMS_INTEGRATION_ENABLED = ccmsIntegrationEnabled;
        this.DTS_INTEGRATION_ENABLED = dtsIntegrationEnabled;
    }

    @Override
    public void run(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId.get());
            if (familySubmissionOptional.isPresent()) {
                Submission familySubmission = familySubmissionOptional.get();
                sendProviderDidNotRespondToFamilyEmail.send(familySubmission);

                log.info("Unidentified Provider submitted response for family submission {}, enqueuing transfer of documents.",
                        familySubmissionId.get());
                familySubmission.getInputData().put("providerResponseSubmissionId", providerSubmission.getId().toString());
                familySubmission.getInputData().put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());
                submissionRepositoryService.save(familySubmission);
                if (DTS_INTEGRATION_ENABLED) {
                    enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository, pdfTransmissionJob,
                            familySubmission, FileNameUtility.getFileNameForPdf(familySubmission, "Provider-Responded"));
                    enqueueDocumentTransfer.enqueueUploadedDocumentBySubmission(userFileRepositoryService,
                            uploadedDocumentTransmissionJob, s3PresignService, familySubmission);
                }
                if (CCMS_INTEGRATION_ENABLED) {
                    // Because this is an unidentified provider, they don't get to upload documents and the job should be
                    // enqueued instantly
                    ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadInstantly(familySubmissionId.get());
                }
            } else {
                log.warn(
                        "Could not find family submission for family submission id {} and provider submission {}. Not sending an unidentified provider email to the family.",
                        familySubmissionId.get(), providerSubmission.getId());
            }
        } else {
            log.warn(
                    "Could not find family submission id for provider submission {}. Not sending an unidentified provider email to the family.",
                    providerSubmission.getId());
        }

        sendUnidentifiedProviderConfirmationEmail.send(providerSubmission);
    }
}
