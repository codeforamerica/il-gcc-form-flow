package org.ilgcc.jobs;

import static org.ilgcc.app.utils.enums.CCMSEndpoints.APP_SUBMISSION_ENDPOINT;

import com.fasterxml.jackson.databind.JsonNode;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.JobrunrJobRepository;
import org.ilgcc.app.data.Transaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.data.UserFileTransaction;
import org.ilgcc.app.data.UserFileTransactionRepositoryService;
import org.ilgcc.app.data.ccms.CCMSApiClient;
import org.ilgcc.app.data.ccms.CCMSJobSchedulingService;
import org.ilgcc.app.data.ccms.CCMSTransaction;
import org.ilgcc.app.data.ccms.CCMSTransactionPayloadService;
import org.ilgcc.app.data.ccms.TransactionFile;
import org.ilgcc.app.pdf.MultiProviderPDFService;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.TransactionStatus;
import org.ilgcc.app.utils.enums.TransactionType;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CCMSSubmissionFetchTransactionJob extends CCMSJobSchedulingService {

    private static final Semaphore concurrencyLimiter = new Semaphore(10);

    public CCMSSubmissionFetchTransactionJob(JobScheduler jobScheduler,
            CCMSTransactionPayloadService ccmsTransactionPayloadService, CCMSApiClient ccmsApiClient,
            TransactionRepositoryService transactionRepositoryService, SubmissionRepositoryService submissionRepositoryService,
            JobrunrJobRepository jobrunrJobRepository, UserFileRepositoryService userFileRepositoryService,
            UserFileTransactionRepositoryService userFileTransactionRepositoryService,
            MultiProviderPDFService multiProviderPDFService, CloudFileRepository cloudFileRepository) {
        super(jobScheduler,
                ccmsTransactionPayloadService, ccmsApiClient,
                transactionRepositoryService, submissionRepositoryService,
                jobrunrJobRepository, userFileRepositoryService,
                userFileTransactionRepositoryService,
                multiProviderPDFService, cloudFileRepository);
    }

    @Override
    public void scheduleJob(@NotNull UUID submissionId, long offsetDelaySeconds, String message) {
        JobId jobId = null;
        jobScheduler.schedule(Instant.now().plus(Duration.ofSeconds(offsetDelaySeconds)),
                () -> fetchCCMsTrasaction(submissionId));

        log.info(String.format("%s %s for submission with ID: %s in %s. CCMS integration enabled: %s", message,
                jobId, submissionId, offsetDelaySeconds, isCCMSIntegrationEnabled()));
    }

    @Job(name = "Send CCMS Submission Payload", retries = 5)
    public void fetchCCMsTrasaction(@NotNull UUID submissionId) throws IOException {
        Transaction existingTransaction = transactionRepositoryService.getBySubmissionId(submissionId);
        if (existingTransaction == null) {
            if (isOnlineNow()) {
                Optional<Submission> submissionOptional = submissionRepositoryService.findById(submissionId);
                if (submissionOptional.isPresent()) {
                    Submission submission = submissionOptional.get();
                    Optional<CCMSTransaction> ccmsTransactionOptional = ccmsTransactionPayloadService.generateSubmissionTransactionPayload(
                            submission);
                    if (ccmsTransactionOptional.isPresent()) {
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
                            }

                            CCMSTransaction ccmsTransaction = ccmsTransactionOptional.get();
                            log.info("Sending submission {} to CCMS", submissionId);
                            JsonNode response = null;
                            if (enableV2Api) {
                                // TODO Do the V2 API thing here
                            } else {
                                response = ccmsApiClient.sendRequest(APP_SUBMISSION_ENDPOINT.getValue(), ccmsTransaction);
                            }
                            log.info("Received response from CCMS when sending transaction payload: {}", response);

                            String workItemId = response.hasNonNull("workItemId") ? response.get("workItemId").asText() : null;

                            if (workItemId == null) {
                                log.warn("Received null work item ID from CCMS transaction for submission : {}",
                                        submissionId);
                            }

                            UUID transactionId = UUID.fromString(response.get("transactionId").asText());
                            Transaction transaction = transactionRepositoryService.createTransaction(transactionId, submissionId,
                                    workItemId, TransactionType.APPLICATION.getValue());
                            List<UserFile> userFiles = ccmsTransaction.getFiles().stream().map(TransactionFile::getUserFile)
                                    .collect(
                                            Collectors.toCollection(ArrayList::new));
                            userFileTransactionRepositoryService.saveAll(
                                    userFiles.stream().map(userFile -> UserFileTransaction.builder()
                                            .userFile(userFile)
                                            .transaction(transaction)
                                            .submission(submission)
                                            .transactionStatus(TransactionStatus.REQUESTED)
                                            .build()).toList()
                            );

                            log.info("All providers responded: {}. {} sent to CCMS with transaction {}",
                                    SubmissionUtilities.haveAllProvidersResponded(submission), submissionId, transactionId);

                        } catch (IOException e) {
                            log.error("There was an error when attempting to send submission {} to CCMS",
                                    submissionId, e);
                            throw e;
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
                        log.error("Could not create CCMS payload for submission : {}", submission.getId());
                        throw new RuntimeException("Could not create CCMS payload for submission " + submission.getId());
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
        } else {
            log.info("Transaction {} already exists for submission {}, skipping CCMS transaction",
                    existingTransaction.getTransactionId(), submissionId);
        }
    }

}