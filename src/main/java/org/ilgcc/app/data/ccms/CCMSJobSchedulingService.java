package org.ilgcc.app.data.ccms;

import jakarta.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.JobrunrJobRepository;
import org.ilgcc.jobs.OfflineTimeRange;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CCMSJobSchedulingService {

    protected final CCMSApiClient ccmsApiClient;
    protected final JobrunrJobRepository jobrunrJobRepository;

    protected int jobDelayMinutes;

    protected static final ZoneId CENTRAL_ZONE = ZoneId.of("America/Chicago");
    protected List<OfflineTimeRange> ccmsOfflineTimeRanges;
    protected int ccmsTransactionDelayOffset;

    // a primitive int isn't threadsafe, but AtomicInteger is. This will ensure multiple threads
    // creating delayed jobs always have the correct time offset.
    protected final AtomicInteger totalCcmsTransactionDelayOffset = new AtomicInteger(0);

    public CCMSJobSchedulingService(CCMSApiClient ccmsApiClient,
            JobrunrJobRepository jobrunrJobRepository) {
        this.ccmsApiClient = ccmsApiClient;
        this.jobrunrJobRepository = jobrunrJobRepository;
    }

    @Getter
    @Setter
    public class JobSchedule {

        private final long offsetDelaySeconds;
        private final String message;

        JobSchedule(long offsetDelaySeconds, String message) {
            this.offsetDelaySeconds = offsetDelaySeconds;
            this.message = message;
        }

    }

    @PostConstruct
    void init() {
        jobDelayMinutes = ccmsApiClient.getConfiguration().getTransactionDelayMinutes();
        ccmsOfflineTimeRanges = ccmsApiClient.getConfiguration().getCcmsOfflineTimeRanges();

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

    public JobSchedule getJobScheduleWithDelay() {
        ZonedDateTime now = ZonedDateTime.now(CENTRAL_ZONE);
        if (isOnlineAt(now.plusMinutes(jobDelayMinutes))) {
            // If CCMS is (back) online *in the future*, make sure that we're resetting the initial total offset for the next time
            // CCMS is offline... but only if a job that might get enqueued *instantly* won't actually be sent during the current
            // offline time range
            if (isOnlineNow()) {
                totalCcmsTransactionDelayOffset.set(ccmsTransactionDelayOffset);
            }
            return new JobSchedule(jobDelayMinutes * 60, "Enqueued Submission CCMS Payload Transaction job with ID: ");
        } else {
            return getJobScheduleWhileOffline();
        }
    }

    public JobSchedule getJobSecheduleWithSecondsOffset(int offsetDelaySeconds) {
        ZonedDateTime now = ZonedDateTime.now(CENTRAL_ZONE);
        if (isOnlineAt(now.plusSeconds(offsetDelaySeconds))) {
            // If CCMS is (back) online *in the future*, make sure that we're resetting the initial total offset for the next time
            // CCMS is offline... but only if a job that might get enqueued *instantly* won't actually be sent during the current
            // offline time range
            if (isOnlineNow()) {
                totalCcmsTransactionDelayOffset.set(ccmsTransactionDelayOffset);
            }

            return new JobSchedule(offsetDelaySeconds, "Enqueued Submission CCMS Payload Transaction job with ID: {} for "
                    + "submission with "
                    + "ID: ");
        } else {
            return getJobScheduleWhileOffline();
        }
    }

    public JobSchedule getJobScheduleInstantly() {
        if (isOnlineNow()) {
            // If CCMS is (back) online, make sure that we're resetting the initial total offset for the next time
            // CCMS is offline... but only a job that might get enqueued *not instantly* won't be delayed into an upcoming
            // offline time range
            if (isOnlineAt(ZonedDateTime.now(CENTRAL_ZONE).plusMinutes(jobDelayMinutes))) {
                totalCcmsTransactionDelayOffset.set(ccmsTransactionDelayOffset);
            }

            return new JobSchedule(0, "Enqueued Submission CCMS Payload Transaction job with ID: {} for submission with ID: ");
        } else {
            return getJobScheduleWhileOffline();
        }
    }

    public JobSchedule getJobScheduleWhileOffline() {
        // We can check if we're still in the offline range and if we are, we will have the number of seconds until the end of the offline range
        long secondsUntilEndOfOfflineRange = getSecondsUntilEndOfOfflineRangeStartingAt(ZonedDateTime.now(CENTRAL_ZONE));

        // At a minimum, delay the job for the total number of seconds accumulated so far. For example, if the ccmsTransactionDelayOffset
        // is 15 seconds and this the first delayed job, we delay 15 seconds. But if this is the 4th delayed job, we would
        // delay 60 seconds (1st delayed 15, 2nd delayed 30, 3rd delayed 45...)

        // And then we can make sure we run this job starting once the offline range ends + the offset caused by prior offline
        // scheduled jobs, so we can have them staggered by the offset
        long jobDelaySeconds = totalCcmsTransactionDelayOffset.get() + secondsUntilEndOfOfflineRange;

        // And finally we can bump up the amount of time we need to wait for the next job. As the number of delayed jobs increases,
        // this will increase.
        totalCcmsTransactionDelayOffset.getAndAdd(ccmsTransactionDelayOffset);

        return new JobSchedule(jobDelaySeconds,
                String.format("CCMS offline for another %s seconds. Enqueued Submission CCMS "
                        + "Payload Transaction job with ID: ", secondsUntilEndOfOfflineRange));
    }

    public boolean isOnlineNow() {
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
