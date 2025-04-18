package org.ilgcc.jobs;

import java.io.IOException;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.SendGridEmailService;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendRecurringEmailJob {

    private final JobScheduler jobScheduler;
    private final SendGridEmailService sendGridEmailService;


    public SendRecurringEmailJob(JobScheduler jobScheduler, SendGridEmailService sendGridEmailService) {
        this.jobScheduler = jobScheduler;
        this.sendGridEmailService = sendGridEmailService;
    }

    public void enqueueSendEmailJob(ILGCCEmail email, Long delay) {
        JobId jobId = jobScheduler.schedule(ZonedDateTime.now().plusMinutes(delay), () -> sendEmailRequest(email));
        log.info("Enqueued {} email job with ID: {} for resource organization with ID: {}", email.getEmailType(), jobId,
                email.getOrgId());

    }

    @Job(name = "Send Email Request", retries = 3)
    public void sendEmailRequest(ILGCCEmail email) throws IOException {
        try {
            sendGridEmailService.sendEmail(email);
            log.info("Successfully sent the {} for resource organization with ID {} to Sendgrid.", email.getEmailType(),
                    email.getOrgId());
        } catch (IOException e) {
            log.error("There was an error when attempting to send the {} for resource organization with ID {}: {}",
                    email.getEmailType(), email.getOrgId(), e.getMessage());
            throw e;
        }
    }
}