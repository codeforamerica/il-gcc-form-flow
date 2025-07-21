package org.ilgcc.jobs;


import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.sendgrid.SendGridEmailService;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendEmailJob {

    private final JobScheduler jobScheduler;
    private final SendGridEmailService sendGridEmailService;

    // Limit to 10 concurrent jobs at once
    private static final Semaphore concurrencyLimiter = new Semaphore(10);

    @Value("${il-gcc.enable-emails}")
    boolean emailsEnabled;

    public SendEmailJob(JobScheduler jobScheduler, SendGridEmailService sendGridEmailService) {
        this.jobScheduler = jobScheduler;
        this.sendGridEmailService = sendGridEmailService;
    }

    public void enqueueSendSubmissionEmailJob(ILGCCEmail email) {
        enqueueSendSubmissionEmailJob(email, 0);
    }

    public void enqueueSendSubmissionEmailJob(ILGCCEmail email, int offsetDelaySeconds) {
        JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofSeconds(offsetDelaySeconds)),
                () -> sendEmailRequest(email));

        log.info("Enqueued {} email job {} for submission {} in {} seconds", email.getEmailType(), jobId,
                email.getSubmissionId(), offsetDelaySeconds);
    }

    public void enqueueSendOrganizationEmailJob(ILGCCEmail email, Long delayInSeconds) {
        JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofSeconds(delayInSeconds)),
                () -> sendEmailRequest(email));

        log.info("Enqueued {} email job {} with for resource organization {}", email.getEmailType(), jobId,
                email.getOrgId());
    }

    @Job(name = "Send Email Request", retries = 3)
    public void sendEmailRequest(ILGCCEmail email) throws IOException {
        String logId;
        String logIdType;

        if (email.getOrgId() != null) {
            logId = email.getOrgId();
            logIdType = "resource org";
        } else {
            logId = email.getSubmissionId().toString();
            logIdType = "submission";
        }

        if (emailsEnabled) {
            boolean acquired = false;
            try {
                // Try to acquire a permit with a timeout
                acquired = concurrencyLimiter.tryAcquire(30, TimeUnit.SECONDS);
                if (!acquired) {
                    log.error("Could not acquire concurrency slot within timeout for email type {} and {} {}. Job will be retried.",
                            email.getEmailType(), logIdType, logId);
                    // Optionally throw an exception to trigger JobRunr retry
                    throw new IOException("Timeout waiting to acquire semaphore permit for sending email.");
                }

                sendGridEmailService.sendEmail(email);
                log.info("Successfully sent the {} for {} {} to Sendgrid.", email.getEmailType(),
                        logIdType, logId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore interrupt flag
                log.error("Interrupted while waiting to acquire concurrency slot", e);
                throw new IOException("Interrupted while waiting to acquire semaphore permit", e);
            } catch (IOException e) {
                log.error("There was an error when attempting to send the {} for {} {}",
                        email.getEmailType(), logIdType, logId, e);
                throw e;
            } finally {
                if (acquired) {
                    concurrencyLimiter.release();
                }
            }
        } else {
            log.info("Emails disabled. Skipping sending {} email for {} {}", email.getEmailType(),
                    logIdType, logId);
            log.info("Would have sent: {} with a subject of: {} from: {}", email.getBody().getValue(), email.getSubject(),
                    email.getSenderEmail().toString()); // Don't log recipient email for security reasons
        }
    }
}
