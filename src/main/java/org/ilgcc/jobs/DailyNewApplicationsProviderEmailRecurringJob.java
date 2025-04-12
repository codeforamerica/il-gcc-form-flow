package org.ilgcc.jobs;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.data.CCMSDataService;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.data.ResourceOrganizationTransaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.email.SendDailyNewApplicationProviderEmail;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DailyNewApplicationsProviderEmailRecurringJob {

    @Autowired
    SendDailyNewApplicationProviderEmail sendDailyNewApplicationProviderEmail;

    private final TransactionRepositoryService transactionRepositoryService;
    private final CCMSDataService ccmsDataService;
    private final SendEmailJob sendEmailJob;

    public DailyNewApplicationsProviderEmailRecurringJob(TransactionRepositoryService transactionRepositoryService,
            CCMSDataService ccmsDataService, SendEmailJob sendEmailJob) {
        this.transactionRepositoryService = transactionRepositoryService;
        this.ccmsDataService = ccmsDataService;
        this.sendEmailJob = sendEmailJob;
    }

    @Recurring(id = "daily-provider-email-job", cron = "0 * * * *")
    @Job(name = "Daily New Applications Email to Providers")
    public void dailyProviderEmailJob() {
        List<ResourceOrganization> activeResourceOrganizations = ccmsDataService.getActiveResourceOrganizations(
                List.of("GG", "QQ"));

        OffsetDateTime currentDate = OffsetDateTime.now().minusHours(24);

        Map<String, List<ResourceOrganizationTransaction>> transmissions = transactionRepositoryService.findSubmissionsSentByResourceOrganizationSince(
                currentDate);

        activeResourceOrganizations.forEach(org -> {
            Map<String, Object> emailData = new HashMap<>();

            //toDo: figure out how to get this data
            List<String> recipients = List.of();
            emailData.put("transactions", transmissions.get(org.getResourceOrgId()));
            emailData.put("processingOrgName", org.getName());
            emailData.put("processingOrgId", org.getResourceOrgId());
            emailData.put("currentEmailDate", currentDate.toString());
            emailData.put("recipients", recipients);

            sendDailyNewApplicationProviderEmail.send(emailData);

        });

    }


}
