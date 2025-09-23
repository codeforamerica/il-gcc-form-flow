package org.ilgcc.jobs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.UserFileRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.CCMSDataService;
import org.ilgcc.app.data.TransactionRepository;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        classes = IlGCCApplication.class
)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DailyNewApplicationsProviderEmailRecurringJobTest {

    @Autowired
    MessageSource messageSource;

    @Autowired
    TransactionRepositoryService transactionRepositoryService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    SubmissionRepository submissionRepository;
    
    @Autowired
    UserFileRepository userFileRepository;

    @Autowired
    CCMSDataService ccmsDataService;

    String TWO_RESOURCE_ORG_EMAILS = "{\"12345678901234\": [\"12345678901234@mail.com\"], \"12345678901235\": [\"12345678901235@mail.com\"]}";

    String FOUR_RESOURCE_ORG_EMAILS = "{\"12345678901234\": [\"12345678901234@mail.com\", \"12345678901234-2@mail.com\", \"12345678901234-3@mail.com\"], \"12345678901235\": [\"12345678901235@mail.com\"]}";


    @MockitoBean
    private SendEmailJob sendEmailJob;

    private DailyNewApplicationsProviderEmailRecurringJob dailyNewApplicationsProviderEmailRecurringJob;

    @BeforeEach
    public void setUp() {
        List<String> submissionsWith12345678901234Org = List.of("A12345", "B12345", "C12345", "D12345", "E12345", "F12345");

        submissionsWith12345678901234Org.forEach(sc -> {
            Submission currentSubmission = new SubmissionTestBuilder()
                    .withParentDetails()
                    .withFlow("gcc")
                    .with("organizationId", "12345678901234")
                    .withSubmittedAtDate(OffsetDateTime.now().minusDays(1))
                    .withShortCode(sc.toString())
                    .build();
            submissionRepository.save(currentSubmission);
        });

        List<String> submissionsWith12345678901235Org = List.of("A67890", "B67890", "C67890", "D67890", "E67890", "F67890");

        submissionsWith12345678901235Org.forEach(sc -> {
            Submission currentSubmission = new SubmissionTestBuilder()
                    .withParentDetails()
                    .withFlow("gcc")
                    .withSubmittedAtDate(OffsetDateTime.now().minusDays(1))
                    .with("organizationId", "12345678901235")
                    .withShortCode(sc.toString())
                    .build();
            submissionRepository.save(currentSubmission);
        });

        List<Submission> allSubmissions = submissionRepository.findAll();

        for (int i = 0; i < allSubmissions.size(); i++) {
            transactionRepositoryService.createTransaction(UUID.randomUUID(), allSubmissions.get(i).getId(),
                    String.format("WI-000%s", i));
        }
    }

    @AfterEach
    protected void clearSubmissions() {
        userFileRepository.deleteAll();
        transactionRepository.deleteAll();
        submissionRepository.deleteAll();
    }

    @Test
    void jobSkipsWhenEnableResourceOrgEmailIsFalse() {
        dailyNewApplicationsProviderEmailRecurringJob = new DailyNewApplicationsProviderEmailRecurringJob(
                transactionRepositoryService, ccmsDataService, sendEmailJob, false, TWO_RESOURCE_ORG_EMAILS);
        dailyNewApplicationsProviderEmailRecurringJob.parseMap();
        dailyNewApplicationsProviderEmailRecurringJob.dailyProviderEmailJob();

        verifyNoInteractions(sendEmailJob);

    }

    @Test
    void jobSkipsWhenResourceEmailsAreNull() {
        dailyNewApplicationsProviderEmailRecurringJob = new DailyNewApplicationsProviderEmailRecurringJob(
                transactionRepositoryService, ccmsDataService, sendEmailJob, true, null);

        dailyNewApplicationsProviderEmailRecurringJob.parseMap();
        dailyNewApplicationsProviderEmailRecurringJob.messageSource = messageSource;

        dailyNewApplicationsProviderEmailRecurringJob.dailyProviderEmailJob();

        verifyNoInteractions(sendEmailJob);
    }

    @Test
    void sendsSingleEmailPerOrgWhenSingleEmailExists() {
        dailyNewApplicationsProviderEmailRecurringJob = new DailyNewApplicationsProviderEmailRecurringJob(
                transactionRepositoryService, ccmsDataService, sendEmailJob, true, TWO_RESOURCE_ORG_EMAILS);

        dailyNewApplicationsProviderEmailRecurringJob.parseMap();
        dailyNewApplicationsProviderEmailRecurringJob.messageSource = messageSource;

        dailyNewApplicationsProviderEmailRecurringJob.dailyProviderEmailJob();

        verify(sendEmailJob, times(2)).enqueueSendOrganizationEmailJob(any(ILGCCEmail.class), anyLong());
    }

    @Test
    void sendSingleEmailPerOrgWhenMultipleEmailsExist() {
        dailyNewApplicationsProviderEmailRecurringJob = new DailyNewApplicationsProviderEmailRecurringJob(
                transactionRepositoryService, ccmsDataService, sendEmailJob,  true, FOUR_RESOURCE_ORG_EMAILS);

        dailyNewApplicationsProviderEmailRecurringJob.parseMap();
        dailyNewApplicationsProviderEmailRecurringJob.messageSource = messageSource;

        dailyNewApplicationsProviderEmailRecurringJob.dailyProviderEmailJob();

        verify(sendEmailJob, times(2)).enqueueSendOrganizationEmailJob(any(ILGCCEmail.class), anyLong());
    }
}
