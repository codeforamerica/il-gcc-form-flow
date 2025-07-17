package org.ilgcc.jobs;


import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.SendGridEmailService;
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

    @Value("${il-gcc.enable-emails}")
    boolean emailsEnabled;

    public SendEmailJob(JobScheduler jobScheduler, SendGridEmailService sendGridEmailService) {
        this.jobScheduler = jobScheduler;
        this.sendGridEmailService = sendGridEmailService;
    }

    public void enqueueSendEmailJob(ILGCCEmail email) {
        enqueueSendEmailJob(email, 0);
    }

    public void enqueueSendEmailJob(ILGCCEmail email, int offsetDelaySeconds) {
        if (emailsEnabled) {
            JobId jobId = jobScheduler.schedule(Instant.now().plus(Duration.ofSeconds(offsetDelaySeconds)),
                    () -> sendEmailRequest(email));

            log.info("Enqueued {} email job with ID: {} for submission with ID: {} in {} seconds", email.getEmailType(), jobId, email.getSubmissionId(), offsetDelaySeconds);
        } else {
            log.info("Emails disabled. Skipping enqueue {} email job for submission with ID: {}", email.getEmailType(),email.getSubmissionId());
            log.info("Would have sent: {} with a subject of: {} from: {}", email.getBody().getValue(), email.getSubject(), email.getSenderEmail().toString()); // Don't log recipient email for security reasons
        }
    }

    @Job(name = "Send Email Request", retries = 3)
    public void sendEmailRequest(ILGCCEmail email) throws IOException {
        try {
            sendGridEmailService.sendEmail(email);
            log.info("Successfully sent the {} for submission with ID {} to Sendgrid.", email.getEmailType(), email.getSubmissionId());
        } catch (IOException e) {
            log.error("There was an error when attempting to send the {} for submission with ID {}", email.getEmailType(), email.getSubmissionId(), e);
            throw e;
        }
    }
}