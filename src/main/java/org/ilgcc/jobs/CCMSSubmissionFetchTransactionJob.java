package org.ilgcc.jobs;

import static org.ilgcc.app.utils.enums.CCMSEndpoints.WORK_ITEM_LOOKUP_ENDPOINT;

import com.fasterxml.jackson.databind.JsonNode;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.Transaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.data.UserFileTransaction;
import org.ilgcc.app.data.UserFileTransactionRepositoryService;
import org.ilgcc.app.data.ccms.CCMSApiClient;
import org.ilgcc.app.data.ccms.CCMSJobSchedulingService;
import org.ilgcc.app.data.ccms.CCMSJobSchedulingService.JobSchedule;
import org.ilgcc.app.data.ccms.CCMSTransactionLookup;
import org.ilgcc.app.data.ccms.CCMSTransactionPayloadService;
import org.ilgcc.app.utils.enums.TransactionStatus;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CCMSSubmissionFetchTransactionJob {

    @Autowired
    UserFileTransactionRepositoryService userFileTransactionRepository;

    @Autowired
    CCMSJobSchedulingService jobSchedulingService;

    private final JobScheduler jobScheduler;
    private final CCMSTransactionPayloadService ccmsTransactionPayloadService;
    private final CCMSApiClient ccmsApiClient;
    private final TransactionRepositoryService transactionRepositoryService;
    private final SubmissionRepositoryService submissionRepositoryService;

    private boolean enableV2Api;

    private static final Semaphore concurrencyLimiter = new Semaphore(10);

    public CCMSSubmissionFetchTransactionJob(JobScheduler jobScheduler,
            CCMSTransactionPayloadService ccmsTransactionPayloadService, CCMSApiClient ccmsApiClient,
            TransactionRepositoryService transactionRepositoryService, SubmissionRepositoryService submissionRepositoryService
    ) {
        this.jobScheduler = jobScheduler;
        this.ccmsTransactionPayloadService = ccmsTransactionPayloadService;
        this.ccmsApiClient = ccmsApiClient;
        this.transactionRepositoryService = transactionRepositoryService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.enableV2Api = ccmsApiClient.getConfiguration().isEnableV2Api();
    }

    public void scheduleJob(@NotNull UUID transactionId, JobSchedule jobSchedule) {
        JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofSeconds(jobSchedule.getOffsetDelaySeconds())),
                () -> fetchCCMSTransaction(transactionId));

        log.info(String.format("%s %s for transaction with ID: %s in %s seconds. CCMS integration enabled: %s",
                jobSchedule.getMessage(),
                jobId, transactionId, jobSchedule.getOffsetDelaySeconds(), isCCMSIntegrationEnabled()));
    }


    @Job(name = "Request CCMS Submission Status", retries = 5)
    public void fetchCCMSTransaction(@NotNull UUID transactionId) throws IOException {
        if (!enableV2Api) {
            return;
        }
        Transaction existingTransaction = transactionRepositoryService.getByTransactionId(transactionId);

        if (existingTransaction == null) {
            throw new RuntimeException("Could not find transaction with ID: " + transactionId);
        } else {
            if (jobSchedulingService.isOnlineNow()) {
                UUID submissionId = existingTransaction.getSubmissionId();
                boolean acquired = false;
                try {
                    // Try to acquire a permit with a timeout
                    acquired = concurrencyLimiter.tryAcquire(30, TimeUnit.SECONDS);
                    if (!acquired) {
                        log.error(
                                "Could not acquire concurrency slot within timeout for submission {}. Job will be retried.",
                                submissionId);
                        // Throw an exception to trigger JobRunr retry
                        throw new IOException("Timeout waiting to acquire semaphore permit for transmitting to CCMS.");

                    } else {     // Create a request for the fetch
                        // Make the actual request
                        try {
                            JsonNode response = ccmsApiClient.fetchRequest(WORK_ITEM_LOOKUP_ENDPOINT.getValue(),
                                    new CCMSTransactionLookup(existingTransaction.getTransactionId()));
                            log.info("Received response from CCMS when requesting to fetch transaction payload: {}", response);

                            JsonNode files = response.hasNonNull("files") ? response.get("files") : null;

                            List<String> fileIds = new ArrayList<>();

                            if (files != null && files.isArray()) {
                                for (JsonNode fileNode : files) {
                                    if (fileNode.hasNonNull("id")) {
                                        fileIds.add(fileNode.get("id").asText());
                                    }
                                }
                            }

                            List<UserFileTransaction> unconfirmedUserFiles =
                                    userFileTransactionRepository.findByTransactionIdAndTransactionStatus(
                                            existingTransaction.getTransactionId(), TransactionStatus.REQUESTED);

                            unconfirmedUserFiles.forEach(userFileTransaction -> {
                                if (fileIds.contains(userFileTransaction.getTransactionFileId())) {
                                    updateUserFilesTransactionStatus(userFileTransaction, existingTransaction,
                                            TransactionStatus.COMPLETED);
                                } else {
                                    updateUserFilesTransactionStatus(userFileTransaction, existingTransaction,
                                            TransactionStatus.FAILED);
                                }
                            });
                        } catch (IOException e) {
                            log.error("There was an error when attempting to send submission {} to CCMS",
                                    submissionId, e);
                            throw e;

                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // restore interrupt flag
                    log.error("Interrupted while waiting to acquire concurrency slot", e);
                    throw new IOException("Interrupted while waiting to acquire semaphore permit", e);
                } finally {
                    if (acquired) {
                        concurrencyLimiter.release();
                    }
                }
            } else {
                log.info(
                        "Skipping CCMS transaction because CCMS is currently offline. Requeuing CCMS Payload Transaction job "
                                + "for transaction {}. CCMS integration enabled {}",
                        transactionId, isCCMSIntegrationEnabled());
                enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(transactionId);
            }
        }
    }

    public void enqueueCCMSTransactionPayloadWithDelay(@NotNull UUID transactionId) {
        scheduleJob(transactionId, jobSchedulingService.getJobScheduleWithDelay());
    }

    public void enqueueCCMSTransactionPayloadWithSecondsOffset(@NotNull UUID transactionId, int offsetDelaySeconds) {
        scheduleJob(transactionId, jobSchedulingService.getJobSecheduleWithSecondsOffset(offsetDelaySeconds));
    }

    public void enqueueCCMSTransactionPayloadInstantly(@NotNull UUID transactionId) {
        scheduleJob(transactionId, jobSchedulingService.getJobScheduleInstantly());
    }

    private void enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(@NotNull UUID transactionId) {
        scheduleJob(transactionId, jobSchedulingService.getJobScheduleWhileOffline());
    }

    private boolean isCCMSIntegrationEnabled() {
        return ccmsApiClient.getConfiguration().isCCMSIntegrationEnabled();
    }

    private void updateUserFilesTransactionStatus(UserFileTransaction userFileTransaction,
            Transaction currentTransaction, TransactionStatus transactionStatus) {
        userFileTransaction.setTransactionStatus(transactionStatus);
        userFileTransaction.setTransaction(currentTransaction);
        userFileTransactionRepository.save(userFileTransaction);
    }

}