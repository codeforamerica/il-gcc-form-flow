package org.ilgcc.jobs;

import static java.util.Collections.emptyList;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.JobrunrJobRepository;
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

@Deprecated
@Slf4j
@Service
public class ProviderResponseRecurringJob {

    private final S3PresignService s3PresignService;
    private final TransmissionRepositoryService transmissionRepositoryService;
    private final TransactionRepositoryService transactionRepositoryService;
    private final UserFileRepositoryService userFileRepositoryService;
    private final UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;
    private final JobrunrJobRepository jobrunrJobRepository;
    private final PdfTransmissionJob pdfTransmissionJob;
    private final EnqueueDocumentTransfer enqueueDocumentTransfer;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransaction;
    private final boolean isCCMSIntegrationEnabled;
    private final boolean isDTSIntegrationEnabled;

    private final SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;

    public ProviderResponseRecurringJob(S3PresignService s3PresignService,
            TransmissionRepositoryService transmissionRepositoryService,
            TransactionRepositoryService transactionRepositoryService,
            UserFileRepositoryService userFileRepositoryService,
            UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob,
            PdfService pdfService,
            CloudFileRepository cloudFileRepository,
            JobrunrJobRepository jobrunrJobRepository,
            PdfTransmissionJob pdfTransmissionJob,
            EnqueueDocumentTransfer enqueueDocumentTransfer,
            SubmissionRepositoryService submissionRepositoryService,
            CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransaction,
            @Value("${il-gcc.ccms-integration-enabled:false}") boolean isCCMSIntegrationEnabled,
            @Value("${il-gcc.dts-integration-enabled}") boolean isDTSIntegrationEnabled,
            SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail) {
        this.s3PresignService = s3PresignService;
        this.transmissionRepositoryService = transmissionRepositoryService;
        this.transactionRepositoryService = transactionRepositoryService;
        this.userFileRepositoryService = userFileRepositoryService;
        this.uploadedDocumentTransmissionJob = uploadedDocumentTransmissionJob;
        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.jobrunrJobRepository = jobrunrJobRepository;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.enqueueDocumentTransfer = enqueueDocumentTransfer;
        this.submissionRepositoryService = submissionRepositoryService;
        this.ccmsSubmissionPayloadTransaction = ccmsSubmissionPayloadTransaction;
        this.isCCMSIntegrationEnabled = isCCMSIntegrationEnabled;
        this.isDTSIntegrationEnabled = isDTSIntegrationEnabled;
        this.sendProviderDidNotRespondToFamilyEmail = sendProviderDidNotRespondToFamilyEmail;
    }

    @Recurring(id = "no-provider-response-job", cron = "0 * * * *")
    @Job(name = "No provider response job")
    public void runNoProviderResponseJob() {

        if (!isDTSIntegrationEnabled && !isCCMSIntegrationEnabled) {
            // Nothing is enabled. This seems wrong!
            log.error("Neither DTS nor CCMS integration is turned on. Why?");
            return;
        }

        log.info("Running No Provider Response Job for expired submissions since now. DTS integration: {} CCMS integration: {}",
                isDTSIntegrationEnabled,
                isCCMSIntegrationEnabled);

        Set<Submission> expiringSubmissionsToSend;
        Set<Submission> submissionsWithoutTransmissions =
                transmissionRepositoryService.findExpiringSubmissionsWithoutTransmission();
        Set<Submission> submissionsWithoutTransactions =
                transactionRepositoryService.findExpiringSubmissionsWithoutTransactions();

        if (submissionsWithoutTransmissions.equals(submissionsWithoutTransactions)) {
            // Happy case, all the submissions are missing both a transmission and a transaction, so just send one entire
            // Set to both DTS and CCMS
            expiringSubmissionsToSend = submissionsWithoutTransmissions;
        } else {
            // It's possible to have Submissions that have be transmitted (DTS) but not transacted (CCMS) or vice versa depending
            // on how/when the Submissions were created and which integration was turned on at the time
            // We want to only send Submissions that are missing both, which is an indicator that the Submission has never ever
            // been sent DTS or CCMS

            // First, get the intersection of Submissions that have both a transmission and a transaction. These are the
            // Submissions we want to send to DTS and CCMS!
            expiringSubmissionsToSend = new HashSet<>(submissionsWithoutTransmissions);
            expiringSubmissionsToSend.retainAll(submissionsWithoutTransactions);

            // Next, we want to log the UUIDs for whichever Submissions aren't getting sent from the two Sets
            Set<UUID> submissionIdsWithoutTransmissions = submissionsWithoutTransmissions.stream().map(Submission::getId)
                    .collect(Collectors.toSet());
            Set<UUID> submissionIdsWithoutTransactions = submissionsWithoutTransactions.stream().map(Submission::getId)
                    .collect(Collectors.toSet());

            // Find Submissions without transmissions, but that apparently have a transaction
            Set<UUID> submissionIdsWithoutTransmissionsOnly = new HashSet<>(submissionIdsWithoutTransmissions);
            submissionIdsWithoutTransmissionsOnly.removeAll(submissionIdsWithoutTransactions);

            // Find Submissions without transactions, but that apparently have a transmission
            Set<UUID> submissionIdsWithoutTransactionsOnly = new HashSet<>(submissionIdsWithoutTransactions);
            submissionIdsWithoutTransactionsOnly.removeAll(submissionIdsWithoutTransmissions);

            // There's not something inherently wrong if these two values mismatch. Initially, while DTS is the only
            // integration on, we will see Submissions without transactions that have a transmission because they were
            // sent to DTS in the prior job run. And when we eventually are CCMS only, the same will happen for Submissions that
            // do not have transmissions. The time when this logging will be helpful is when both are turned on, and somehow
            // we have a Submission that was sent to DTS and not to CCMS or vice versa in a prior run!
            log.debug(
                    "Submissions without transmissions and transactions do not match. Sending {} submissions. Ignoring {} without transmissions. Ignoring {} without transactions.",
                    expiringSubmissionsToSend.size(), submissionIdsWithoutTransmissionsOnly.size(),
                    submissionIdsWithoutTransactionsOnly.size());

            if (!submissionIdsWithoutTransmissionsOnly.isEmpty()) {
                log.debug("Ignored {} submissions without transmissions. [{}]", submissionIdsWithoutTransmissionsOnly.size(),
                        submissionIdsWithoutTransmissionsOnly);
            }

            if (!submissionIdsWithoutTransactionsOnly.isEmpty()) {
                log.debug("Ignored {} submissions without transactions. [{}]", submissionIdsWithoutTransactionsOnly.size(),
                        submissionIdsWithoutTransactionsOnly);
            }
        }

        log.info(String.format("Running the 'No provider response job' for %s expired submissions",
                expiringSubmissionsToSend.size()));

        if (expiringSubmissionsToSend.isEmpty()) {
            return;
        } else {
            for (Submission expiredFamilySubmission : expiringSubmissionsToSend) {
                if (!hasProviderResponse(expiredFamilySubmission)) {
                    log.info("No provider response found for family submission {}. DTS: {} CCMS: {}",
                            expiredFamilySubmission.getId(),
                            isDTSIntegrationEnabled,
                            isCCMSIntegrationEnabled);

                    if (isDTSIntegrationEnabled) {
                        enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository,
                                pdfTransmissionJob,
                                expiredFamilySubmission,
                                FileNameUtility.getFileNameForPdf(expiredFamilySubmission, "No-Provider-Response"));
                        enqueueDocumentTransfer.enqueueUploadedDocumentBySubmission(userFileRepositoryService,
                                uploadedDocumentTransmissionJob, s3PresignService, expiredFamilySubmission);
                    }
                    if (isCCMSIntegrationEnabled) {
                        ccmsSubmissionPayloadTransaction.enqueueCCMSTransactionPayloadInstantly(
                                expiredFamilySubmission.getId());
                    }
                    updateProviderStatus(expiredFamilySubmission);
                    if (expiredFamilySubmission.getInputData().containsKey("providers")) {
                        List<Map<String, Object>> providersSubflowData = (List<Map<String, Object>>)
                                expiredFamilySubmission.getInputData().getOrDefault("providers", emptyList());
                        providersSubflowData.forEach(provider -> {
                            if (!SubmissionStatus.RESPONDED.name().equals(provider.get("providerResponseStatus"))) {
                                log.info("Sending did not respond email for provider ID: {} for family submission ID: {}",
                                        provider.get("uuid"), expiredFamilySubmission.getId());
                                sendProviderDidNotRespondToFamilyEmail.send(expiredFamilySubmission, "providers",
                                        provider.get("uuid").toString());
                            }
                        });
                    } else {
                        sendProviderDidNotRespondToFamilyEmail.send(expiredFamilySubmission);
                    }


                } else {
                    log.warn(
                            String.format(
                                    "ProviderResponseRecurringJob: The Family and Provider Applications were submitted but they do not have a corresponding transmission or transaction. Check familySubmission: %s",
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
