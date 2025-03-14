package org.ilgcc.jobs;

import static org.ilgcc.app.utils.enums.CCMSEndpoints.APP_SUBMISSION_ENDPOINT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import formflow.library.data.Submission;
import java.time.Duration;
import java.time.Instant;
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
    @Value("${il-gcc.ccms-transaction-delay-minutes}")
    private int jobDelayMinutes;

    public CCMSSubmissionPayloadTransactionJob(
            JobScheduler jobScheduler, CCMSTransactionPayloadService ccmsTransactionPayloadService, CCMSApiClient ccmsApiClient,
            TransactionRepositoryService transactionRepositoryService) {
        this.jobScheduler = jobScheduler;
        this.ccmsTransactionPayloadService = ccmsTransactionPayloadService;
        this.ccmsApiClient = ccmsApiClient;
        this.transactionRepositoryService = transactionRepositoryService;
    }

    public void enqueueCCMSTransactionPayloadWithDelay(Submission submission) {
        JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofMinutes(jobDelayMinutes)),
                () -> sendCCMSTransaction(submission));
        log.info("Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: {}", jobId, submission.getId());
    }

    public void enqueueSubmissionCCMSPayloadTransactionJobInstantly(Submission submission) {
        JobId jobId = jobScheduler.enqueue(() -> sendCCMSTransaction(submission));
        log.info("Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: {}", jobId, submission.getId());
    }

    @Job(name = "Send CCMS Submission Payload", retries = 3)
    public void sendCCMSTransaction(Submission submission) throws JsonProcessingException {
        CCMSTransaction ccmsTransaction = ccmsTransactionPayloadService.generateSubmissionTransactionPayload(submission);
        JsonNode response = ccmsApiClient.sendRequest(APP_SUBMISSION_ENDPOINT.getValue(), ccmsTransaction);
        log.info("Received response from CCMS when sending transaction payload: {}", response);
        transactionRepositoryService.createTransaction(UUID.fromString(response.get("transactionId").asText()), submission.getId());
    }
}