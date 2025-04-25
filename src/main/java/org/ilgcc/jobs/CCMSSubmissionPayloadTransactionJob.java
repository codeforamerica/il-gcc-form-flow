package org.ilgcc.jobs;

import static org.ilgcc.app.utils.enums.CCMSEndpoints.APP_SUBMISSION_ENDPOINT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.data.ccms.CCMSApiClient;
import org.ilgcc.app.data.ccms.CCMSTransaction;
import org.ilgcc.app.data.ccms.CCMSTransactionPayloadService;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CCMSSubmissionPayloadTransactionJob {
    
    private final JobScheduler jobScheduler;
    private final CCMSTransactionPayloadService ccmsTransactionPayloadService;
    private final CCMSApiClient ccmsApiClient;
    private final TransactionRepositoryService transactionRepositoryService;
    private final SubmissionRepositoryService submissionRepositoryService;

    @Value("${il-gcc.ccms-transaction-delay-minutes}")
    private int jobDelayMinutes;

    public CCMSSubmissionPayloadTransactionJob(
            JobScheduler jobScheduler, CCMSTransactionPayloadService ccmsTransactionPayloadService, CCMSApiClient ccmsApiClient,
            TransactionRepositoryService transactionRepositoryService, SubmissionRepositoryService submissionRepositoryService) {
        this.jobScheduler = jobScheduler;
        this.ccmsTransactionPayloadService = ccmsTransactionPayloadService;
        this.ccmsApiClient = ccmsApiClient;
        this.transactionRepositoryService = transactionRepositoryService;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    public void enqueueCCMSTransactionPayloadWithDelay(@NotNull UUID submissionId) {
        JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofMinutes(jobDelayMinutes)),
                () -> sendCCMSTransaction(submissionId));
        log.info("Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: {}", jobId, submissionId);
    }

    public void enqueueSubmissionCCMSPayloadTransactionJobInstantly(@NotNull UUID submissionId) {
        JobId jobId = jobScheduler.enqueue(() -> sendCCMSTransaction(submissionId));
        log.info("Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: {}", jobId, submissionId);
    }

    @Job(name = "Send CCMS Submission Payload", retries = 3)
    public void sendCCMSTransaction(@NotNull UUID submissionId) throws JsonProcessingException {
        Optional<Submission> submissionOptional = submissionRepositoryService.findById(submissionId);
        if (submissionOptional.isPresent()) {
            Submission submission = submissionOptional.get();
            Optional<CCMSTransaction> ccmsTransactionOptional = ccmsTransactionPayloadService.generateSubmissionTransactionPayload(submission);
            if (ccmsTransactionOptional.isPresent()) {
                CCMSTransaction ccmsTransaction = ccmsTransactionOptional.get();
                JsonNode response = ccmsApiClient.sendRequest(APP_SUBMISSION_ENDPOINT.getValue(), ccmsTransaction);
                log.info("Received response from CCMS when sending transaction payload: {}", response);

                String workItemId = response.hasNonNull("workItemId") ? response.get("workItemId").asText() : null;

                if (workItemId == null) {
                    log.warn("Received null work item ID from CCMS transaction for submission : {}", submission.getId());
                }

                transactionRepositoryService.createTransaction(UUID.fromString(response.get("transactionId").asText()),
                        submission.getId(), workItemId);
            } else {
                log.warn("Could not create CCMS payload for submission : {}", submission.getId());
            }
        } else {
            throw new RuntimeException("Could not find submission with ID: " + submissionId);
        }
    }
}