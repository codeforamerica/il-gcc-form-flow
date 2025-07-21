package org.ilgcc.jobs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.CCMSDataService;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.data.ResourceOrganizationTransaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.templates.DailyNewApplicationsProviderEmailTemplate;
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
    boolean enableResourceOrganizationEmails;


    private String orgEmailsRaw;
    private Map<String, List<String>> organizationEmailRecipients = new HashMap<>();

    @Value("${il-gcc.resource-org-emails-delay-seconds}")
    private int secondsOffset;

    @Value("${il-gcc.resource-org-emails-delay-seconds}")
    private int secondsOffsetIncrement;

    @PostConstruct
    void parseMap() {
        if (orgEmailsRaw != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                organizationEmailRecipients = mapper.readValue(orgEmailsRaw, new TypeReference<Map<String, List<String>>>() {
                });
            } catch (Exception e) {
                log.error(
                        "DailyNewApplicationProviderEmailRecurringJob: Could not parse the recipient emails provided. Make sure you set the RESOURCE_ORG_EMAILS environment variable properly.");
            }
        } else {
            organizationEmailRecipients = new HashMap<>();
        }
    }

    public DailyNewApplicationsProviderEmailRecurringJob(TransactionRepositoryService transactionRepositoryService,
            CCMSDataService ccmsDataService, SendRecurringEmailJob sendRecurringEmailJob,
            @Value("${il-gcc.enable-resource-org-emails:false}") boolean enableResourceOrganizationEmails,
            @Value("${il-gcc.resource-org-emails}") String orgEmailsRaw) {
        this.transactionRepositoryService = transactionRepositoryService;
        this.ccmsDataService = ccmsDataService;
        this.sendRecurringEmailJob = sendRecurringEmailJob;
        this.enableResourceOrganizationEmails = enableResourceOrganizationEmails;
        this.orgEmailsRaw = orgEmailsRaw;
    }


    @Recurring(id = "daily-provider-email-job", cron = "0 0 4 * * *", zoneId = "America/Chicago")
    @Job(name = "Daily New Applications Email to Providers")
    public void dailyProviderEmailJob() {
        if (!enableResourceOrganizationEmails) {
            log.info("daily-provider-email-job is off");
            return;
        }

        if (null == orgEmailsRaw) {
            log.info("daily-provider-email-job cannot run because emails have not been set in the environment variables");
            return;
        }


        if (organizationEmailRecipients.isEmpty()) {
            log.debug("There are no email recipients in this current database, so no one to send an email to");
            return;
        }

        OffsetDateTime currentDate = OffsetDateTime.now();
        OffsetDateTime transactionsAsOfDate = currentDate.minusHours(24).withHour(0).withMinute(0).withSecond(0).withNano(0);

        Optional<Map<String, List<ResourceOrganizationTransaction>>> transactions = transactionRepositoryService.findTransactionsByResourceOrganizationsOnDate(
                transactionsAsOfDate);

        List<ResourceOrganization> activeResourceOrganizations = ccmsDataService.getActiveResourceOrganizations();

        organizationEmailRecipients.keySet().forEach(orgId -> {

            List<String> currentOrgRecipients = organizationEmailRecipients.get(orgId);
            Optional<ResourceOrganization> currentOrg = activeResourceOrganizations.stream().filter(org -> org.getResourceOrgId().equals(new BigInteger(orgId))).findFirst();

            if (null == currentOrgRecipients || currentOrgRecipients.isEmpty()) {
                log.info("We don't have any email recipients for {}. Skipping email", orgId);
                return;
            }

            if (currentOrg.isEmpty()) {
                log.info("The org id doesn't have a corresponding org: {} in the database. Skipping email", orgId);
                return;
            }
            enqueueOrgEmail(generateEmailData(transactions, currentOrg.get(), transactionsAsOfDate, currentDate),
                    currentOrgRecipients);
        });
    }

    public Map<String, Object> generateEmailData(Optional<Map<String, List<ResourceOrganizationTransaction>>> transactions,
            ResourceOrganization org, OffsetDateTime transactionsAsOfDate, OffsetDateTime currentDate) {

        List<ResourceOrganizationTransaction> transactionsPerOrganization =
                transactions.isPresent() ? transactions.get().get(org.getResourceOrgId().toString()) : Collections.emptyList();
        Map<String, Object> emailData = new HashMap<>();

        emailData.put("transactions",
                transactionsPerOrganization == null ? Collections.emptyList() : transactionsPerOrganization);
        emailData.put("processingOrgName", org.getName());
        emailData.put("processingOrgId", org.getResourceOrgId());
        emailData.put("transactionsAsOfDate", transactionsAsOfDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        emailData.put("currentEmailDate", currentDate.format(DateTimeFormatter.ofPattern("M/d/yy")));

        return emailData;
    }

    private void enqueueOrgEmail(Map<String, Object> emailData, List<String> recipients) {
        log.info("SendDailyNewApplicationsProviderEmail enqueuing {} emails for {} and processing org: {}", recipients.size(),
                emailData.get("currentEmailDate"), emailData.get("processingOrgName"));

        ILGCCEmail email = new ILGCCEmail(recipients,
                new DailyNewApplicationsProviderEmailTemplate(emailData, messageSource).createTemplate(),
                emailData.get("processingOrgId").toString());
        log.info("secondsOffset: {}", secondsOffset);
        sendRecurringEmailJob.enqueueSendEmailJob(email, Integer.toUnsignedLong(secondsOffset));
        secondsOffset = secondsOffset + secondsOffsetIncrement;
    }
}
