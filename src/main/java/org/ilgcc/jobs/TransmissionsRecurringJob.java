package org.ilgcc.jobs;


import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.email.SendProviderDidNotRespondToFamilyEmail;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.FileNameUtility;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransmissionsRecurringJob {

    private final S3PresignService s3PresignService;
    private final TransmissionRepositoryService transmissionRepositoryService;
    private final TransactionRepositoryService transactionRepositoryService;
    private final UserFileRepositoryService userFileRepositoryService;
    private final UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;
    private final PdfTransmissionJob pdfTransmissionJob;
    private final EnqueueDocumentTransfer enqueueDocumentTransfer;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransaction;
    private final boolean CCMS_INTEGRATION_ENABLED;
    private final boolean DTS_INTEGRATION_ENABLED;

    private final  SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;

    public TransmissionsRecurringJob(S3PresignService s3PresignService,
            TransmissionRepositoryService transmissionRepositoryService,
            TransactionRepositoryService transactionRepositoryService,
            UserFileRepositoryService userFileRepositoryService,
            UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob,
            PdfService pdfService,
            CloudFileRepository cloudFileRepository,
            PdfTransmissionJob pdfTransmissionJob,
            EnqueueDocumentTransfer enqueueDocumentTransfer,
            SubmissionRepositoryService submissionRepositoryService,
            CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransaction,
            @Value("${il-gcc.ccms-integration-enabled:false}") boolean CCMS_INTEGRATION_ENABLED,
            @Value("${il-gcc.dts-integration-enabled}") boolean DTS_INTEGRATION_ENABLED, SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail) {
        this.s3PresignService = s3PresignService;
        this.transmissionRepositoryService = transmissionRepositoryService;
        this.transactionRepositoryService = transactionRepositoryService;
        this.userFileRepositoryService = userFileRepositoryService;
        this.uploadedDocumentTransmissionJob = uploadedDocumentTransmissionJob;
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.enqueueDocumentTransfer = enqueueDocumentTransfer;
        this.submissionRepositoryService = submissionRepositoryService;
        this.ccmsSubmissionPayloadTransaction = ccmsSubmissionPayloadTransaction;
        this.CCMS_INTEGRATION_ENABLED = CCMS_INTEGRATION_ENABLED;
        this.DTS_INTEGRATION_ENABLED = DTS_INTEGRATION_ENABLED;
        this.sendProviderDidNotRespondToFamilyEmail = sendProviderDidNotRespondToFamilyEmail;
    }

    @Recurring(id = "no-provider-response-job", cron = "0 * * * *")
    @Job(name = "No provider response job")
    public void noProviderResponseJob() {
        List<Submission> submissionsWithoutTransmissions = transmissionRepositoryService.findSubmissionsWithoutTransmission();
        List<Submission> submissionsWithoutTransactions = transactionRepositoryService.findSubmissionsWithoutTransmission();

        Set<Submission> submissionsWithoutTransmissionsOrTransactions = new HashSet<>(submissionsWithoutTransmissions);
        submissionsWithoutTransmissionsOrTransactions.addAll(submissionsWithoutTransactions);

        List<Submission> expiredSubmissionsWithNoTransmissionsOrTransactions = submissionsWithoutTransmissionsOrTransactions.stream()
                .filter(ProviderSubmissionUtilities::providerApplicationHasExpired).toList();

        log.info(String.format("Running the 'No provider response job' for %s expired submissions",
                expiredSubmissionsWithNoTransmissionsOrTransactions.size()));

        if (expiredSubmissionsWithNoTransmissionsOrTransactions.isEmpty()) {
            return;
        } else {
            for (Submission submission : expiredSubmissionsWithNoTransmissionsOrTransactions) {
                if (!hasProviderResponse(submission)) {
                    if (DTS_INTEGRATION_ENABLED) {
                        enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository,
                                pdfTransmissionJob,
                                submission, FileNameUtility.getFileNameForPdf(submission, "No-Provider-Response"));
                        enqueueDocumentTransfer.enqueueUploadedDocumentBySubmission(userFileRepositoryService,
                                uploadedDocumentTransmissionJob, s3PresignService, submission);
                    }
                    if (CCMS_INTEGRATION_ENABLED) {
                        ccmsSubmissionPayloadTransaction.enqueueSubmissionCCMSPayloadTransactionJobInstantly(submission);
                    }
                    updateProviderStatus(submission);
                    sendProviderDidNotRespondToFamilyEmail.send(submission);
                } else {
                    log.warn(
                            String.format("TransmissionsRecurringJob: The Family and Provider Applications were submitted but they do not have a corresponding transmission. Check familySubmission: %s",
                                    submission.getId()));
                }
            }
        }
    }

    private void updateProviderStatus(Submission familySubmission) {
        familySubmission.getInputData().put("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.name());
        submissionRepositoryService.save(familySubmission);
    }

    private boolean hasProviderResponse(Submission familySubmission) {
        String providerResponseSubmissionId = (String) familySubmission.getInputData().get("providerResponseSubmissionId");

        if (providerResponseSubmissionId != null) {
            Optional<Submission> providerSubmission = submissionRepositoryService.findById(
                    UUID.fromString(providerResponseSubmissionId));
            return providerSubmission.isPresent() && providerSubmission.get().getSubmittedAt() != null;
        }

        return false;
    }
}
