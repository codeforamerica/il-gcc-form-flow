package org.ilgcc.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kotlin.collections.EmptyList;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.CCMSDataService;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.data.ResourceOrganizationTransaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.templates.DailyNewApplicationsProviderEmailTemplate;
import org.ilgcc.app.utils.DateUtilities;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DailyNewApplicationsProviderEmailRecurringJob {

    @Autowired
    MessageSource messageSource;
    private final TransactionRepositoryService transactionRepositoryService;
    private final CCMSDataService ccmsDataService;
    private final SendRecurringEmailJob sendRecurringEmailJob;
    boolean emailsEnabled;
    boolean enableResourceOrganizationEmails;

    @Value("${il-gcc.resource-org-emails}")
    private String orgEmailsRaw;
    private Map<String, List<String>> organizationEmailRecipients = new HashMap<>();
    @PostConstruct
    void parseMap() throws JsonProcessingException {
        if (orgEmailsRaw != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                organizationEmailRecipients = mapper.readValue(
                        orgEmailsRaw,
                        new TypeReference<Map<String, List<String>>>() {}
                );
            } catch (Exception e) {
                System.err.println("Failed to parse orgEmailsRaw: " + orgEmailsRaw);
                e.printStackTrace();
                throw new RuntimeException("Failed to parse organizationEmailRecipients from ORG_EMAILS", e);
            }
        } else {
            organizationEmailRecipients = new HashMap<>();
        }
    }

    public DailyNewApplicationsProviderEmailRecurringJob(TransactionRepositoryService transactionRepositoryService,
            CCMSDataService ccmsDataService, SendRecurringEmailJob sendRecurringEmailJob,
            @Value("${il-gcc.enable-emails}") boolean emailsEnabled,
            @Value("${il-gcc.enable-resource-org-emails:false}") boolean enableResourceOrganizationEmails) {
        this.transactionRepositoryService = transactionRepositoryService;
        this.ccmsDataService = ccmsDataService;
        this.sendRecurringEmailJob = sendRecurringEmailJob;
        this.emailsEnabled = emailsEnabled;
        this.enableResourceOrganizationEmails = enableResourceOrganizationEmails;
    }

    @Recurring(id = "daily-provider-email-job", cron = "0 0 17 * * *", zoneId = "America/Chicago")
    @Job(name = "Daily New Applications Email to Providers")
    public void dailyProviderEmailJob() {
        if (!emailsEnabled || !enableResourceOrganizationEmails) {
            log.info("daily-provider-email-job is off");
            return;
        }

        if(null == orgEmailsRaw){
            log.info("daily-provider-email-job cannot run because emails have not been set in the environment variables");
            return;
        }

        List<ResourceOrganization> activeResourceOrganizations = ccmsDataService.getActiveResourceOrganizations();

        if(activeResourceOrganizations.isEmpty()){
            log.debug("There are no active resources in this current database, so no one to send an email to");
            return;
        }

        // we want this to run every 24 hours, so it will check for the last 24 hours
        OffsetDateTime currentDate = OffsetDateTime.now();
        OffsetDateTime transactionsAsOfDate = currentDate.minusHours(24);

        Optional<Map<String, List<ResourceOrganizationTransaction>>> transactions = transactionRepositoryService.findSubmissionsSentByResourceOrganizationSince(
                transactionsAsOfDate);

        activeResourceOrganizations.forEach(org -> {
            Map<String, Object> emailData = new HashMap<>();

            List<String> currentOrgRecipients = organizationEmailRecipients.get(org.getResourceOrgId().toString());

            emailData.put("transactions", transactions.isPresent() ? transactions.get().get(org.getResourceOrgId()) : Collections.emptyList());
            emailData.put("processingOrgName", org.getName());
            emailData.put("processingOrgId", org.getResourceOrgId());
            emailData.put("transactionsAsOfDate", transactionsAsOfDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            emailData.put("currentEmailDate", currentDate.format(DateTimeFormatter.ofPattern("M/d/yy")));
            emailData.put("recipients", currentOrgRecipients);

            if(currentOrgRecipients.isEmpty()){
                log.info("We don't have any email recipients for {}. Skipping email", org.getName());
                return;
            }

            enqueueOrgEmail(emailData);
        });
    }

    private void enqueueOrgEmail(Map<String, Object> emailData) {
        List<String> recipients = (List) emailData.get("recipients");

        log.info("SendDailyNewApplicationsProviderEmail enqueuing %s emails for %s and processing org: %s", recipients.size(),
                emailData.get("currentEmailDate"), emailData.get("processingOrgName"));

        recipients.forEach(recipient -> {
            ILGCCEmail email = new ILGCCEmail(recipient, new DailyNewApplicationsProviderEmailTemplate(emailData,
                    messageSource).createTemplate(), emailData.get("processingOrgId").toString());
            sendRecurringEmailJob.enqueueSendEmailJob(email);
        });

    }
}
