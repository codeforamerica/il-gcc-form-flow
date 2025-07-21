package org.ilgcc.jobs;

import java.io.IOException;
import java.time.ZonedDateTime;
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
public class SendRecurringEmailJob {

    private final JobScheduler jobScheduler;
    private final SendGridEmailService sendGridEmailService;

    @Value("${il-gcc.enable-emails}")
    boolean emailsEnabled;

    public SendRecurringEmailJob(JobScheduler jobScheduler, SendGridEmailService sendGridEmailService) {
        this.jobScheduler = jobScheduler;
        this.sendGridEmailService = sendGridEmailService;
    }

    public void enqueueSendEmailJob(ILGCCEmail email, Long delayInSeconds) {
        ZonedDateTime nowPlus = ZonedDateTime.now().plusSeconds(delayInSeconds);
        log.info("nowPlus: {}", nowPlus);
        JobId jobId = jobScheduler.schedule(nowPlus, () -> sendEmailRequest(email));
        log.info("Enqueued {} email job with ID: {} for resource organization with ID: {}", email.getEmailType(), jobId,
                email.getOrgId());

    }

    @Job(name = "Send Email Request", retries = 3)
    public void sendEmailRequest(ILGCCEmail email) throws IOException {
        if (emailsEnabled) {
            try {
                log.info("Sending email request.");
                sendGridEmailService.sendEmail(email);
                log.info("Successfully sent the {} for resource organization with ID {} to Sendgrid.", email.getEmailType(),
                        email.getOrgId());
            } catch (IOException e) {
                log.error("There was an error when attempting to send the {} for resource organization with ID {}",
                        email.getEmailType(), email.getOrgId(), e);
                throw e;
            }
        } else {
            log.info("Emails disabled. Skipping sending {} email for submission with ID: {}", email.getEmailType(),
                    email.getSubmissionId());
            log.info("Would have sent: {} with a subject of: {} from: {}", email.getBody().getValue(), email.getSubject(),
                    email.getSenderEmail().toString()); // Don't log recipient email for security reasons
        }
    }
}