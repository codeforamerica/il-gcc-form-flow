package org.ilgcc.jobs;

import static org.ilgcc.app.utils.enums.CCMSEndpoints.APP_SUBMISSION_ENDPOINT;

import com.fasterxml.jackson.databind.JsonNode;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import org.ilgcc.app.data.ccms.CCMSTransaction;
import org.ilgcc.app.data.ccms.CCMSTransactionPayloadService;
import org.ilgcc.app.utils.SubmissionUtilities;
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

    public void scheduleJob(@NotNull UUID submissionId, JobSchedule jobSchedule) {
        JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofSeconds(jobSchedule.getOffsetDelaySeconds())),
                () -> fetchCCMSTransaction(submissionId));

        log.info(String.format("%s %s for submission with ID: %s in %s. CCMS integration enabled: %s", jobSchedule.getMessage(),
                jobId, submissionId, jobSchedule.getOffsetDelaySeconds(), isCCMSIntegrationEnabled()));
    }


    @Job(name = "Request CCMS Submission Status", retries = 5)
    public void fetchCCMSTransaction(@NotNull UUID submissionId) throws IOException {
        if (!enableV2Api) {
            return;
        }
        Transaction existingTransaction = transactionRepositoryService.getBySubmissionId(submissionId);

        if (jobSchedulingService.isOnlineNow()) {
            Optional<Submission> submissionOptional = submissionRepositoryService.findById(submissionId);
            if (submissionOptional.isPresent()) {
                Submission submission = submissionOptional.get();
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
                        Optional<CCMSTransaction> ccmsTransactionOptional = ccmsTransactionPayloadService.generateSubmissionTransactionPayload(
                                submission);

                        if (ccmsTransactionOptional.isPresent()) {
                            // Make the actual request
                            try {
                                JsonNode response = ccmsApiClient.sendRequest(APP_SUBMISSION_ENDPOINT.getValue(),
                                        ccmsTransactionOptional.get());
                                log.info("Received response from CCMS when sending transaction payload: {}", response);

                                // parse to get the files
                                List<String> filesIds = Collections.emptyList();
//                                    response.hasNonNull("workItemId") ? response.get("workItemId").asText() : null;

                                List<UserFileTransaction> unconfirmedUserFiles =
                                        userFileTransactionRepository.findIncompleteStatusByTransaction(
                                                existingTransaction.getTransactionId());

                                unconfirmedUserFiles.forEach(userFileTransaction -> {
                                    if (filesIds.contains(userFileTransaction.getTransactionFileId())) {
                                        userFileTransaction.setTransactionStatus(TransactionStatus.COMPLETED);
                                        userFileTransaction.setTransaction(existingTransaction);
                                        userFileTransactionRepository.save(userFileTransaction);
                                    }
                                });

                                List<UserFileTransaction> failedUserFiles =
                                        userFileTransactionRepository.findByTransactionIdAndTransactionStatus(
                                                existingTransaction.getTransactionId(), TransactionStatus.REQUESTED);

                                unconfirmedUserFiles.forEach(userFileTransaction -> {
                                    if (filesIds.contains(userFileTransaction.getTransactionFileId())) {
                                        userFileTransaction.setTransactionStatus(TransactionStatus.FAILED);
                                        userFileTransaction.setTransaction(existingTransaction);
                                        userFileTransactionRepository.save(userFileTransaction);
                                    }
                                });

                                log.info("All providers responded: {}. {} sent to CCMS with transaction {}",
                                        SubmissionUtilities.haveAllProvidersResponded(submission), submissionId,
                                        existingTransaction.getTransactionId());


                            } catch (IOException e) {
                                log.error("There was an error when attempting to send submission {} to CCMS",
                                        submissionId, e);
                                throw e;

                            }
                        } else {
                            log.error("Could not create CCMS payload for submission : {}", submission.getId());
                            throw new RuntimeException("Could not create CCMS payload for submission " + submission.getId());
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
                throw new RuntimeException("Could not find submission with ID: " + submissionId);
            }
        } else {
            log.info(
                    "Skipping CCMS transaction because CCMS is currently offline. Requeuing CCMS Payload Transaction job for {}. CCMS integration enabled {}",
                    submissionId, isCCMSIntegrationEnabled());
            enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(submissionId);
        }


    }

    public void enqueueCCMSTransactionPayloadWithDelay(@NotNull UUID submissionId) {
        scheduleJob(submissionId, jobSchedulingService.getJobScheduleWithDelay());
    }

    public void enqueueCCMSTransactionPayloadWithSecondsOffset(@NotNull UUID submissionId, int offsetDelaySeconds) {
        scheduleJob(submissionId, jobSchedulingService.getJobSecheduleWithSecondsOffset(offsetDelaySeconds));
    }

    public void enqueueCCMSTransactionPayloadInstantly(@NotNull UUID submissionId) {
        scheduleJob(submissionId, jobSchedulingService.getJobScheduleInstantly());
    }

    private void enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(@NotNull UUID submissionId) {
        scheduleJob(submissionId, jobSchedulingService.getJobScheduleWhileOffline());
    }

    private boolean isCCMSIntegrationEnabled() {
        return ccmsApiClient.getConfiguration().isCCMSIntegrationEnabled();
    }

}