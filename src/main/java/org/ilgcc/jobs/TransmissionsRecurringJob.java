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
import java.util.stream.Collectors;
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

        Set<Submission> unsentSubmissions;

        if (!DTS_INTEGRATION_ENABLED && !CCMS_INTEGRATION_ENABLED) {
            // Nothing is enabled. This seems wrong!
            log.error("Neither DTS nor CCMS integration is turned on. Why?");
            return;
        }

        log.info("Running No Provider Response Job. DTS integration: {} CCMS integration: {}", DTS_INTEGRATION_ENABLED, CCMS_INTEGRATION_ENABLED);

        Set<Submission> submissionsWithoutTransmissions = transmissionRepositoryService.findSubmissionsWithoutTransmission();
        Set<Submission> submissionsWithoutTransactions = transactionRepositoryService.findSubmissionsWithoutTransaction();

        if (submissionsWithoutTransmissions.equals(submissionsWithoutTransactions)) {
            // Happy case, all the submissions are missing both a transmission and a transaction, so just send one entire
            // Set to both DTS and CCMS
            unsentSubmissions = submissionsWithoutTransmissions;
        } else {
            // It's possible to have Submissions that have be transmitted (DTS) but not transacted (CCMS) or vice versa depending
            // on how/when the Submissions were created and which integration was turned on at the time
            // We want to only send Submissions that are missing both, which is an indicator that the Submission has never ever
            // been sent DTS or CCMS

            // First, get the intersection of Submissions that have both a transmission and a transaction. These are the
            // Submissions we want to send to DTS and CCMS!
            unsentSubmissions = new HashSet<>(submissionsWithoutTransmissions);
            unsentSubmissions.retainAll(submissionsWithoutTransactions);

            // Next, we want to log the UUIDs for whichever Submissions aren't getting sent from the two Sets
            Set<UUID> submissionIdsWithoutTransmissions = submissionsWithoutTransmissions.stream().map(Submission::getId).collect(Collectors.toSet());
            Set<UUID> submissionIdsWithoutTransactions = submissionsWithoutTransactions.stream().map(Submission::getId).collect(Collectors.toSet());

            // Find Submissions without transmissions, but that apparently have a transaction
            Set<UUID> submissionIdsWithoutTransmissionsOnly = new HashSet<>(submissionIdsWithoutTransmissions);
            submissionIdsWithoutTransmissionsOnly.removeAll(submissionIdsWithoutTransactions);

            // Find Submissions without transactions, but that apparently have a transmission
            Set<UUID> submissionIdsWithoutTransactionsOnly = new HashSet<>(submissionIdsWithoutTransactions);
            submissionIdsWithoutTransactionsOnly.removeAll(submissionIdsWithoutTransmissions);

            log.warn(
                    "Submissions without transmissions and transactions do not match. Sending {} submissions. Ignoring {} without transmissions. Ignoring {} without transactions.",
                    unsentSubmissions.size(), submissionIdsWithoutTransmissionsOnly.size(),
                    submissionIdsWithoutTransactionsOnly.size());

            if (!submissionIdsWithoutTransmissionsOnly.isEmpty()) {
                log.warn("Ignored {} submissions without transmissions. [{}]", submissionIdsWithoutTransmissionsOnly.size(), submissionIdsWithoutTransmissionsOnly);
            }

            if (!submissionIdsWithoutTransactionsOnly.isEmpty()) {
                log.warn("Ignored {} submissions without transactions. [{}]", submissionIdsWithoutTransactionsOnly.size(), submissionIdsWithoutTransactionsOnly);
            }
        }

        List<Submission> expiredUnsentSubmissions = unsentSubmissions.stream()
                .filter(ProviderSubmissionUtilities::providerApplicationHasExpired).toList();

        log.info(String.format("Running the 'No provider response job' for %s expired submissions",
                expiredUnsentSubmissions.size()));

        if (expiredUnsentSubmissions.isEmpty()) {
            return;
        } else {
            for (Submission expiredFamilySubmission : expiredUnsentSubmissions) {
                if (!hasProviderResponse(expiredFamilySubmission)) {
                    log.info("No provider response found for {}. DTS: {} CCMS {}", expiredFamilySubmission.getId(), DTS_INTEGRATION_ENABLED, CCMS_INTEGRATION_ENABLED);
                    if (DTS_INTEGRATION_ENABLED) {
                        enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository,
                                pdfTransmissionJob,
                                expiredFamilySubmission, FileNameUtility.getFileNameForPdf(expiredFamilySubmission, "No-Provider-Response"));
                        enqueueDocumentTransfer.enqueueUploadedDocumentBySubmission(userFileRepositoryService,
                                uploadedDocumentTransmissionJob, s3PresignService, expiredFamilySubmission);
                    }
                    if (CCMS_INTEGRATION_ENABLED) {
                        ccmsSubmissionPayloadTransaction.enqueueSubmissionCCMSPayloadTransactionJobInstantly(expiredFamilySubmission);
                    }
                    updateProviderStatus(expiredFamilySubmission);
                    sendProviderDidNotRespondToFamilyEmail.send(expiredFamilySubmission);
                } else {
                    log.warn(
                            String.format("TransmissionsRecurringJob: The Family and Provider Applications were submitted but they do not have a corresponding transmission or transaction. Check familySubmission: %s",
                                    expiredFamilySubmission.getId()));
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
