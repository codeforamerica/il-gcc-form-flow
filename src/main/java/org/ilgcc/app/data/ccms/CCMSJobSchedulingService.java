package org.ilgcc.app.data.ccms;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SchedulePreparerUtility.getRelatedChildrenSchedulesForEachProvider;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.JobrunrJobRepository;
import org.ilgcc.app.data.Transaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.data.UserFileTransaction;
import org.ilgcc.app.data.UserFileTransactionRepositoryService;
import org.ilgcc.app.email.SendFamilyApplicationTransmittedConfirmationEmail;
import org.ilgcc.app.email.SendFamilyApplicationTransmittedProviderConfirmationEmail;
import org.ilgcc.app.pdf.MultiProviderPDFService;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.jobs.CCMSSubmissionFetchTransactionJob;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.ilgcc.jobs.OfflineTimeRange;
import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CCMSJobSchedulingService {

    protected final JobScheduler jobScheduler;
    protected final CCMSTransactionPayloadService ccmsTransactionPayloadService;
    protected final CCMSApiClient ccmsApiClient;
    protected final TransactionRepositoryService transactionRepositoryService;
    protected final SubmissionRepositoryService submissionRepositoryService;
    protected final JobrunrJobRepository jobrunrJobRepository;
    protected final UserFileRepositoryService userFileRepositoryService;
    protected final UserFileTransactionRepositoryService userFileTransactionRepositoryService;

    protected final MultiProviderPDFService multiProviderPDFService;
    CloudFileRepository cloudFileRepository;

    protected int jobDelayMinutes;

    protected boolean enableV2Api;

    protected static final ZoneId CENTRAL_ZONE = ZoneId.of("America/Chicago");
    protected List<OfflineTimeRange> ccmsOfflineTimeRanges;
    protected int ccmsTransactionDelayOffset;

    // a primitive int isn't threadsafe, but AtomicInteger is. This will ensure multiple threads
    // creating delayed jobs always have the correct time offset.
    protected final AtomicInteger totalCcmsTransactionDelayOffset = new AtomicInteger(0);

    @Autowired
    SendFamilyApplicationTransmittedConfirmationEmail sendFamilyApplicationTransmittedConfirmationEmail;

    @Autowired
    SendFamilyApplicationTransmittedProviderConfirmationEmail sendFamilyApplicationTransmittedProviderConfirmationEmail;

    // Limit to 10 concurrent jobs at once
    protected static final Semaphore concurrencyLimiter = new Semaphore(10);

    public CCMSJobSchedulingService(JobScheduler jobScheduler,
            CCMSTransactionPayloadService ccmsTransactionPayloadService, CCMSApiClient ccmsApiClient,
            TransactionRepositoryService transactionRepositoryService, SubmissionRepositoryService submissionRepositoryService,
            JobrunrJobRepository jobrunrJobRepository, UserFileRepositoryService userFileRepositoryService,
            UserFileTransactionRepositoryService userFileTransactionRepositoryService,
            MultiProviderPDFService multiProviderPDFService, CloudFileRepository cloudFileRepository) {
        this.jobScheduler = jobScheduler;
        this.ccmsTransactionPayloadService = ccmsTransactionPayloadService;
        this.ccmsApiClient = ccmsApiClient;
        this.transactionRepositoryService = transactionRepositoryService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.jobrunrJobRepository = jobrunrJobRepository;
        this.userFileRepositoryService = userFileRepositoryService;
        this.userFileTransactionRepositoryService = userFileTransactionRepositoryService;
        this.multiProviderPDFService = multiProviderPDFService;
        this.cloudFileRepository = cloudFileRepository;
    }

    @PostConstruct
    void init() {
        jobDelayMinutes = ccmsApiClient.getConfiguration().getTransactionDelayMinutes();
        ccmsOfflineTimeRanges = ccmsApiClient.getConfiguration().getCcmsOfflineTimeRanges();
        enableV2Api = ccmsApiClient.getConfiguration().isEnableV2Api();

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

    public void scheduleJob(@NotNull UUID submissionId, long offsetDelaySeconds, String message) {
        JobId jobId = null;
//                jobScheduler.schedule(Instant.now().plus(Duration.ofSeconds(offsetDelaySeconds)),
//                () -> sendCCMSTransaction(submissionId));

        if (jobId == null) {
            log.warn("The schedule job method has not been defined for the current transaction job");
        }
        log.info(String.format("%s %s for submission with ID: %s in %s. CCMS integration enabled: %s", message,
                jobId, submissionId, offsetDelaySeconds, isCCMSIntegrationEnabled()));
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
            scheduleJob(submissionId, jobDelayMinutes * 60, "Enqueued Submission CCMS Payload Transaction job with ID: ");

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

            scheduleJob(submissionId, offsetDelaySeconds, "Enqueued Submission CCMS Payload Transaction job with ID: {} for "
                    + "submission with "
                    + "ID: ");
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

            scheduleJob(submissionId, 0,"Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: ");
        } else {
            enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(submissionId);
        }
    }

    public void enqueueSubmissionCCMSPayloadTransactionJobWhileOffline(@NotNull UUID submissionId) {
        // We can check if we're still in the offline range and if we are, we will have the number of seconds until the end of the offline range
        long secondsUntilEndOfOfflineRange = getSecondsUntilEndOfOfflineRangeStartingAt(ZonedDateTime.now(CENTRAL_ZONE));

        // At a minimum, delay the job for the total number of seconds accumulated so far. For example, if the ccmsTransactionDelayOffset
        // is 15 seconds and this the first delayed job, we delay 15 seconds. But if this is the 4th delayed job, we would
        // delay 60 seconds (1st delayed 15, 2nd delayed 30, 3rd delayed 45...)

        // And then we can make sure we run this job starting once the offline range ends + the offset caused by prior offline
        // scheduled jobs, so we can have them staggered by the offset
        long jobDelaySeconds = totalCcmsTransactionDelayOffset.get() + secondsUntilEndOfOfflineRange;

        scheduleJob(submissionId, (int) jobDelaySeconds, String.format("CCMS offline for another %s seconds. Enqueued Submission CCMS "
                + "Payload Transaction job with ID: ", secondsUntilEndOfOfflineRange));

        // And finally we can bump up the amount of time we need to wait for the next job. As the number of delayed jobs increases,
        // this will increase.
        totalCcmsTransactionDelayOffset.getAndAdd(ccmsTransactionDelayOffset);
    }

    protected boolean isOnlineNow() {
        return isOnlineAt(ZonedDateTime.now(CENTRAL_ZONE));
    }

    protected boolean isOnlineAt(ZonedDateTime dateTime) {
        return ccmsApiClient.getConfiguration().isOnlineAt(dateTime);
    }

    protected boolean isCCMSIntegrationEnabled() {
        return ccmsApiClient.getConfiguration().isCCMSIntegrationEnabled();
    }

    protected long getSecondsUntilEndOfOfflineRangeStartingAt(ZonedDateTime startTime) {
        return ccmsApiClient.getConfiguration().getSecondsUntilEndOfOfflineRangeStartingAt(startTime);
    }

}
