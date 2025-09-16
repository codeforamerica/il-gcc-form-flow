package org.ilgcc.jobs;

import static org.ilgcc.app.utils.SchedulePreparerUtility.getRelatedChildrenSchedulesForEachProvider;
import static org.ilgcc.app.utils.constants.MediaTypes.PDF_CONTENT_TYPE;
import static org.ilgcc.app.utils.enums.CCMSEndpoints.APP_SUBMISSION_ENDPOINT;

import com.fasterxml.jackson.databind.JsonNode;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.file.CloudFileRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.JobrunrJobRepository;
import org.ilgcc.app.data.Transaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.data.ccms.CCMSApiClient;
import org.ilgcc.app.data.ccms.CCMSTransaction;
import org.ilgcc.app.data.ccms.CCMSTransactionPayloadService;
import org.ilgcc.app.email.SendFamilyApplicationTransmittedConfirmationEmail;
import org.ilgcc.app.email.SendFamilyApplicationTransmittedProviderConfirmationEmail;
import org.ilgcc.app.pdf.MultiProviderPDFService;
import org.ilgcc.app.utils.ByteArrayMultipartFile;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class CCMSSubmissionPayloadTransactionJob {

    private final JobScheduler jobScheduler;
    private final CCMSTransactionPayloadService ccmsTransactionPayloadService;
    private final CCMSApiClient ccmsApiClient;
    private final TransactionRepositoryService transactionRepositoryService;
    private final SubmissionRepositoryService submissionRepositoryService;
    private final JobrunrJobRepository jobrunrJobRepository;

    private final MultiProviderPDFService multiProviderPDFService;
    CloudFileRepository cloudFileRepository;

    private int jobDelayMinutes;
    
    private boolean v2ApiEnabled;

    private static final ZoneId CENTRAL_ZONE = ZoneId.of("America/Chicago");
    private List<OfflineTimeRange> ccmsOfflineTimeRanges;
    private int ccmsTransactionDelayOffset;

    // a primitive int isn't threadsafe, but AtomicInteger is. This will ensure multiple threads
    // creating delayed jobs always have the correct time offset.
    private final AtomicInteger totalCcmsTransactionDelayOffset = new AtomicInteger(0);

    @Autowired
    SendFamilyApplicationTransmittedConfirmationEmail sendFamilyApplicationTransmittedConfirmationEmail;

    @Autowired
    SendFamilyApplicationTransmittedProviderConfirmationEmail sendFamilyApplicationTransmittedProviderConfirmationEmail;

    // Limit to 10 concurrent jobs at once
    private static final Semaphore concurrencyLimiter = new Semaphore(10);

    public CCMSSubmissionPayloadTransactionJob(JobScheduler jobScheduler,
            CCMSTransactionPayloadService ccmsTransactionPayloadService, CCMSApiClient ccmsApiClient,
            TransactionRepositoryService transactionRepositoryService, SubmissionRepositoryService submissionRepositoryService,
            JobrunrJobRepository jobrunrJobRepository,
            MultiProviderPDFService multiProviderPDFService, CloudFileRepository cloudFileRepository) {
        this.jobScheduler = jobScheduler;
        this.ccmsTransactionPayloadService = ccmsTransactionPayloadService;
        this.ccmsApiClient = ccmsApiClient;
        this.transactionRepositoryService = transactionRepositoryService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.jobrunrJobRepository = jobrunrJobRepository;
        this.multiProviderPDFService = multiProviderPDFService;
        this.cloudFileRepository = cloudFileRepository;
    }

    @PostConstruct
    void init() {
        jobDelayMinutes = ccmsApiClient.getConfiguration().getTransactionDelayMinutes();
        ccmsOfflineTimeRanges = ccmsApiClient.getConfiguration().getCcmsOfflineTimeRanges();
        v2ApiEnabled = ccmsApiClient.getConfiguration().isEnableV2Api();

        // On startup, if we have offline time ranges...
        if (ccmsOfflineTimeRanges != null && !ccmsOfflineTimeRanges.isEmpty()) {
            int previouslyScheduledCCMSJobs = jobrunrJobRepository.getScheduledCCMSJobCount();
            log.info("Found {} previously scheduled CCMS jobs", previouslyScheduledCCMSJobs);

            // this value should be one offset unit, plus if there are previously scheduled jobs and the instance was restarted,
            // add those into the equation.
            ccmsTransactionDelayOffset = ccmsApiClient.getConfiguration().getOfflineTransactionDelayOffset();

            totalCcmsTransactionDelayOffset.set(
                    ccmsTransactionDelayOffset + (ccmsTransactionDelayOffset * previouslyScheduledCCMSJobs));
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
                totalCcmsTransactionDelayOffset.set(ccmsTransactionDelayOffset);
            }

            JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofMinutes(jobDelayMinutes)),
                    () -> sendCCMSTransaction(submissionId));
            log.info("Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: {} in {} minutes",
                    jobId, submissionId, jobDelayMinutes);
        } else {
            enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(submissionId);
        }
    }

    public void enqueueCCMSTransactionPayloadWithSecondsOffset(@NotNull UUID submissionId, int offsetDelaySeconds) {
        ZonedDateTime now = ZonedDateTime.now(CENTRAL_ZONE);
        if (isOnlineAt(now.plusSeconds(offsetDelaySeconds))) {
            // If CCMS is (back) online *in the future*, make sure that we're resetting the initial total offset for the next time
            // CCMS is offline... but only if a job that might get enqueued *instantly* won't actually be sent during the current
            // offline time range
            if (isOnlineNow()) {
                totalCcmsTransactionDelayOffset.set(ccmsTransactionDelayOffset);
            }

            JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofSeconds(offsetDelaySeconds)),
                    () -> sendCCMSTransaction(submissionId));
            log.info("Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: {} in {} seconds",
                    jobId, submissionId, offsetDelaySeconds);
        } else {
            enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(submissionId);
        }
    }

    public void enqueueCCMSTransactionPayloadInstantly(@NotNull UUID submissionId) {
        if (isOnlineNow()) {
            // If CCMS is (back) online, make sure that we're resetting the initial total offset for the next time
            // CCMS is offline... but only a job that might get enqueued *not instantly* won't be delayed into an upcoming
            // offline time range
            if (isOnlineAt(ZonedDateTime.now(CENTRAL_ZONE).plusMinutes(jobDelayMinutes))) {
                totalCcmsTransactionDelayOffset.set(ccmsTransactionDelayOffset);
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
        long jobDelaySeconds = totalCcmsTransactionDelayOffset.get() + secondsUntilEndOfOfflineRange;

        JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofSeconds(jobDelaySeconds)),
                () -> sendCCMSTransaction(submissionId));

        log.info(
                "CCMS offline for another {} seconds. Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: {} in: {} seconds",
                secondsUntilEndOfOfflineRange, jobId, submissionId, jobDelaySeconds);

        // And finally we can bump up the amount of time we need to wait for the next job. As the number of delayed jobs increases,
        // this will increase.
        totalCcmsTransactionDelayOffset.getAndAdd(ccmsTransactionDelayOffset);
    }

    @Job(name = "Send CCMS Submission Payload", retries = 5)
    public void sendCCMSTransaction(@NotNull UUID submissionId) throws IOException {
        Transaction existingTransaction = transactionRepositoryService.getBySubmissionId(submissionId);
        if (existingTransaction == null) {
            if (isOnlineNow()) {
                Optional<Submission> submissionOptional = submissionRepositoryService.findById(submissionId);
                if (submissionOptional.isPresent()) {
                    Submission submission = submissionOptional.get();

                    CompletableFuture.runAsync(() -> {
                        try {
                            // Put the submission pdf asynchronously into S3 as a backup
                            // Do not fail on exceptions, as this is a backup just in case
                            // CCMS / CMS doesn't process the submission properly on the backend
                            Map<String, byte[]> pdfs = multiProviderPDFService.generatePDFs(submission);

                            for (String pdfFileName : pdfs.keySet()) {
                                MultipartFile multipartFile = new ByteArrayMultipartFile(pdfs.get(pdfFileName), pdfFileName,
                                        PDF_CONTENT_TYPE);
                                String s3ZipPath = SubmissionUtilities.generatePdfPath(pdfFileName, submission.getId());

                                cloudFileRepository.upload(s3ZipPath, multipartFile);
                            }
                        } catch (IOException | InterruptedException e) {
                            log.error("Error uploading submission {} to S3", submissionId, e);
                        }
                    });

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
                            if (v2ApiEnabled) {
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
                            transactionRepositoryService.createTransaction(transactionId, submissionId, workItemId);

                            log.info("All providers responded: {}. {} sent to CCMS with transaction {}",
                                    SubmissionUtilities.haveAllProvidersResponded(submission), submissionId, transactionId);

                            if (!SubmissionUtilities.isNoProviderSubmission(submission.getInputData())) {
                                // If there is 1+ provider, send an email letting the family know what the provider(s) did
                                sendFamilyApplicationTransmittedConfirmationEmail.send(submission);
                                enqueueProviderEmails(submission);
                            }

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
        return ccmsApiClient.getConfiguration().isOnlineAt(dateTime);
    }

    private long getSecondsUntilEndOfOfflineRangeStartingAt(ZonedDateTime startTime) {
        return ccmsApiClient.getConfiguration().getSecondsUntilEndOfOfflineRangeStartingAt(startTime);
    }

    private void enqueueProviderEmails(Submission familySubmission) {
        List<String> providersWithSchedules =
                getRelatedChildrenSchedulesForEachProvider(familySubmission.getInputData()).keySet().stream()
                        .filter(id -> !id.equals("NO_PROVIDER")).toList();

        if (!providersWithSchedules.isEmpty()) {
            for (String providerId : providersWithSchedules) {
                Map<String, Object> providerObject = SubmissionUtilities.getCurrentProvider(familySubmission.getInputData(),
                        providerId);

                if (providerObject.containsKey(("providerResponseSubmissionId"))) {
                    // Only send the provider email if the provider responded
                    String providerResponseSubmissionId = providerObject.get("providerResponseSubmissionId").toString();
                    Optional<Submission> providerSubmission = submissionRepositoryService.findById(
                            UUID.fromString(providerResponseSubmissionId));
                    if (providerSubmission.isPresent() && providerSubmission.get().getInputData().getOrDefault(
                            "providerResponseAgreeToCare", "false").equals("true")) {
                        sendFamilyApplicationTransmittedProviderConfirmationEmail.send(providerSubmission.get());
                    }
                }
            }
        }

        if (familySubmission.getInputData().containsKey("providerResponseSubmissionId")) {
            Optional<Submission> providerSubmission =
                    submissionRepositoryService.findById(UUID.fromString(familySubmission.getInputData().get(
                            "providerResponseSubmissionId").toString()));
            if (providerSubmission.isPresent() && providerSubmission.get().getInputData().getOrDefault(
                    "providerResponseAgreeToCare", "false").equals("true")) {
                sendFamilyApplicationTransmittedProviderConfirmationEmail.send(providerSubmission.get());
            }
        }


    }
}