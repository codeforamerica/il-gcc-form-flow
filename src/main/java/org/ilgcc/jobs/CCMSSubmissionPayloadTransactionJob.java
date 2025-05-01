package org.ilgcc.jobs;

import static org.ilgcc.app.utils.enums.CCMSEndpoints.APP_SUBMISSION_ENDPOINT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.JobrunrJobRepository;
import org.ilgcc.app.data.Transaction;
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
    private final JobrunrJobRepository jobrunrJobRepository;

    @Value("${il-gcc.ccms-transaction-delay-minutes}")
    private int jobDelayMinutes;

    @Value("${il-gcc.ccms-offline-time-ranges}")
    private String ccmsOfflineTimeRangesJson;

    private static final ZoneId CENTRAL_ZONE = ZoneId.of("America/Chicago");
    private List<OfflineTimeRange> ccmsOfflineTimeRanges;

    @Value("${il-gcc.ccms-offline-transaction-delay-offset:15}")
    private int ccmsTransactionDelayOffset;

    private int totalCcmsTransactionDelayOffset;

    public CCMSSubmissionPayloadTransactionJob(JobScheduler jobScheduler,
            CCMSTransactionPayloadService ccmsTransactionPayloadService, CCMSApiClient ccmsApiClient,
            TransactionRepositoryService transactionRepositoryService, SubmissionRepositoryService submissionRepositoryService,
            JobrunrJobRepository jobrunrJobRepository) {
        this.jobScheduler = jobScheduler;
        this.ccmsTransactionPayloadService = ccmsTransactionPayloadService;
        this.ccmsApiClient = ccmsApiClient;
        this.transactionRepositoryService = transactionRepositoryService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.jobrunrJobRepository = jobrunrJobRepository;
    }

    @PostConstruct
    void init() {
        // On startup, if we have offline time ranges, we need to load them from the json
        if (ccmsOfflineTimeRangesJson != null) {
            try {
                log.info("Parsing CCMS offline times: " + ccmsOfflineTimeRangesJson);
                ObjectMapper objectMapper = new ObjectMapper();
                this.ccmsOfflineTimeRanges = objectMapper.readValue(ccmsOfflineTimeRangesJson, new TypeReference<>() {
                });
                log.info("Parsed CCMS offline times. Number of ranges: {}", ccmsOfflineTimeRanges.size());

                int previouslyScheduledCCMSJobs = jobrunrJobRepository.getScheduledCCMSJobCount();
                log.info("Found {} previously scheduled CCMS jobs", previouslyScheduledCCMSJobs);

                // this value should be one offset unit, plus if there are previously scheduled jobs and the instance was restarted,
                // add those into the equation.
                totalCcmsTransactionDelayOffset =
                        ccmsTransactionDelayOffset + (ccmsTransactionDelayOffset * previouslyScheduledCCMSJobs);
            } catch (JsonProcessingException e) {
                log.error(
                        "CCMSSubmissionPayloadTransactionJob: Could not parse CCMS offline times. Make sure you set the CCMS_OFFLINE_TIME_RANGES environment variable properly.",
                        e);
            }
        } else {
            ccmsOfflineTimeRanges = new ArrayList<>();
        }
    }

    public void enqueueCCMSTransactionPayloadWithDelay(@NotNull UUID submissionId) {
        ZonedDateTime now = ZonedDateTime.now(CENTRAL_ZONE);
        if (isOnlineAt(now.plusMinutes(jobDelayMinutes))) {
            // If CCMS is (back) online *in the future*, make sure that we're resetting the initial total offset for the next time
            // CCMS is offline... but only if a job that might get enqueued *instantly* won't actually be sent during the current
            // offline time range
            if (isOnlineNow()) {
                totalCcmsTransactionDelayOffset = ccmsTransactionDelayOffset;
            }

            JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofMinutes(jobDelayMinutes)),
                    () -> sendCCMSTransaction(submissionId));
            log.info("Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: {} in {} minutes",
                    jobId, submissionId, jobDelayMinutes);
        } else {
            enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(submissionId);
        }
    }

    public void enqueueSubmissionCCMSPayloadTransactionJobInstantly(@NotNull UUID submissionId) {
        if (isOnlineNow()) {
            // If CCMS is (back) online, make sure that we're resetting the initial total offset for the next time
            // CCMS is offline... but only a job that might get enqueued *not instantly* won't be delayed into an upcoming
            // offline time range
            if (isOnlineAt(ZonedDateTime.now(CENTRAL_ZONE).plusMinutes(jobDelayMinutes))) {
                totalCcmsTransactionDelayOffset = ccmsTransactionDelayOffset;
            }

            JobId jobId = jobScheduler.enqueue(() -> sendCCMSTransaction(submissionId));
            log.info("Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: {}", jobId,
                    submissionId);

        } else {
            enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(submissionId);
        }
    }

    private void enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(@NotNull UUID submissionId) {
        // We can check if we're still in the offline range and if we are, we will have the number of seconds until the end of the offline range
        long secondsUntilEndOfOfflineRange = getSecondsUntilEndOfOfflineRangeStartingAt(ZonedDateTime.now(CENTRAL_ZONE));

        // At a minimum, delay the job for the total number of seconds accumulated so far. For example, if the ccmsTransactionDelayOffset
        // is 15 seconds and this the first delayed job, we delay 15 seconds. But if this is the 4th delayed job, we would
        // delay 60 seconds (1st delayed 15, 2nd delayed 30, 3rd delayed 45...)

        // And then we can make sure we run this job starting once the offline range ends + the offset caused by prior offline
        // scheduled jobs, so we can have them staggered by the offset
        long jobDelaySeconds = totalCcmsTransactionDelayOffset + secondsUntilEndOfOfflineRange;

        JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofSeconds(jobDelaySeconds)),
                () -> sendCCMSTransaction(submissionId));

        log.info(
                "CCMS offline for another {} seconds. Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: {} in: {} seconds",
                secondsUntilEndOfOfflineRange, jobId, submissionId, jobDelaySeconds);

        // And finally we can bump up the amount of time we need to wait for the next job. As the number of delayed jobs increases,
        // this will increase.
        totalCcmsTransactionDelayOffset = totalCcmsTransactionDelayOffset + ccmsTransactionDelayOffset;
    }

    @Job(name = "Send CCMS Submission Payload", retries = 3)
    public void sendCCMSTransaction(@NotNull UUID submissionId) throws JsonProcessingException {
        Transaction existingTransaction = transactionRepositoryService.getBySubmissionId(submissionId);
        if (existingTransaction == null) {
            if (isOnlineNow()) {
                Optional<Submission> submissionOptional = submissionRepositoryService.findById(submissionId);
                if (submissionOptional.isPresent()) {
                    Submission submission = submissionOptional.get();
                    Optional<CCMSTransaction> ccmsTransactionOptional = ccmsTransactionPayloadService.generateSubmissionTransactionPayload(
                            submission);
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
            } else {
                log.info(
                        "Skipping CCMS transaction because CCMS is currently offline. Requeuing CCMS Payload Transaction job for {}",
                        submissionId);
                enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(submissionId);
            }
        } else {
            log.info("Transaction {} already exists for submission {}, skipping CCMS transaction",
                    existingTransaction.getTransactionId(), submissionId);
        }
    }

    private boolean isOnlineNow() {
        return isOnlineAt(ZonedDateTime.now(CENTRAL_ZONE));
    }

    private boolean isOnlineAt(ZonedDateTime dateTime) {
        return !ccmsOfflineTimeRanges.stream().anyMatch(range -> range.isTimeWithinRange(dateTime));
    }

    private long getSecondsUntilEndOfOfflineRangeStartingAt(ZonedDateTime startTime) {
        Optional<Long> seconds = ccmsOfflineTimeRanges.stream().filter(range -> range.isTimeWithinRange(startTime))
                .map(range -> range.secondsUntilEnd(startTime)).filter(Objects::nonNull).findFirst();
        return seconds.isPresent() ? seconds.get() : 0;
    }
}