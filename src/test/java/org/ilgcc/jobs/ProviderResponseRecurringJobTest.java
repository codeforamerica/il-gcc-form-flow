package org.ilgcc.jobs;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.TransactionRepository;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.SendProviderDidNotRespondToFamilyEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(
        classes = IlGCCApplication.class
)
@ActiveProfiles("test")
public class ProviderResponseRecurringJobTest {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionRepositoryService transactionRepositoryService;

    @Autowired
    private ApplicationContext context;

    private ProviderResponseRecurringJob providerResponseRecurringJob;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    @MockitoBean
    private CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob;

    private Submission activeWithPastExpirationDate;

    private Submission transmittedSubmission;
    private Submission unsubmittedSubmission;
    private Submission activeWithFutureExpirationDate;

    private Submission expiredUntransmittedSubmissionWithProviderResponse;
    private Submission providerSubmission;

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    MessageSource messageSource;

    private SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;

    @BeforeEach
    void setUp() {
        sendProviderDidNotRespondToFamilyEmail = new SendProviderDidNotRespondToFamilyEmail(sendEmailJob, messageSource,
                submissionRepositoryService);
        providerResponseRecurringJob = new ProviderResponseRecurringJob(
                transactionRepositoryService,
                submissionRepositoryService,
                ccmsSubmissionPayloadTransactionJob,
                true,
                sendProviderDidNotRespondToFamilyEmail
        );
    }

    @AfterEach
    protected void clearSubmissions() {
        transactionRepository.deleteAll();
        submissionRepository.deleteAll();
    }

    @Test
    void doesNotSendExpiredUntransmittedSubmissionWithProviderResponseToCCMS() {

        providerSubmission = new SubmissionTestBuilder().withProviderSubmissionData()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(2)).build();
        submissionRepository.save(providerSubmission);

        expiredUntransmittedSubmissionWithProviderResponse = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId().toString())
                .build();
        submissionRepository.save(expiredUntransmittedSubmissionWithProviderResponse);

        providerResponseRecurringJob.runNoProviderResponseJob();

        verifyNoInteractions(sendEmailJob);
    }

    @Test
    void doesNotSendUnexpiredOrUnsubmittedApplicationsToCCMS() {
        activeWithFutureExpirationDate = new SubmissionTestBuilder()
                .withParentDetails()
                .with("parentContactEmail", "test-unexpired@mail.com")
                .with("providerResponseStatus", SubmissionStatus.ACTIVE.name())
                .with("providerApplicationResponseExpirationDate", OffsetDateTime.now().plusDays(3))
                .withSubmittedAtDate(OffsetDateTime.now())
                .withFlow("gcc")
                .build();
        submissionRepository.save(activeWithFutureExpirationDate);

        unsubmittedSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .with("parentContactEmail", "test-unsubmitted@mail.com")
                .withFlow("gcc")
                .build();
        submissionRepository.save(unsubmittedSubmission);

        providerResponseRecurringJob.runNoProviderResponseJob();

        verify(ccmsSubmissionPayloadTransactionJob,
                never()).enqueueCCMSTransactionPayloadInstantly(eq(activeWithFutureExpirationDate.getId()));
        verify(ccmsSubmissionPayloadTransactionJob,
                never()).enqueueCCMSTransactionPayloadInstantly(eq(unsubmittedSubmission.getId()));


    }

    @Nested
    class whenNoMultipleProviders {

        @BeforeEach
        void setUp() {
            OffsetDateTime expiredSubmissionDate = ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(OffsetDateTime.now())
                    .minusMinutes(5);
            activeWithPastExpirationDate = new SubmissionTestBuilder()
                    .withParentDetails()
                    .with("parentContactEmail", "test-expired@mail.com")
                    .with("providerResponseStatus", SubmissionStatus.ACTIVE.name())
                    .with("providerApplicationResponseExpirationDate", expiredSubmissionDate)
                    .withSubmittedAtDate(expiredSubmissionDate)
                    .withFlow("gcc")
                    .build();
            submissionRepository.save(activeWithPastExpirationDate);
        }

        @AfterEach
        protected void clearSubmissions() {
            submissionRepository.deleteAll();
        }

        @ParameterizedTest
        @MethodSource("inactiveSubmissionStatuses")
        public void whenSubmissionHasExpirationDateInPast(SubmissionStatus status) {
            activeWithPastExpirationDate.getInputData().put("providerResponseStatus", status.name());
            submissionRepositoryService.save(activeWithPastExpirationDate);

            providerResponseRecurringJob.runNoProviderResponseJob();

            verify(ccmsSubmissionPayloadTransactionJob,
                    never()).enqueueCCMSTransactionPayloadInstantly(eq(activeWithPastExpirationDate.getId()));
            verify(sendEmailJob, never()).enqueueSendEmailJob(any(ILGCCEmail.class));

        }

        static Stream<Arguments> inactiveSubmissionStatuses() {
            return Stream.of(SubmissionStatus.values()).filter(s -> !s.name().equals(SubmissionStatus.ACTIVE.name()))
                    .map(status -> Arguments.of(status));
        }

        @Test
        void sendToCCMSIsOnlyCalledOnSubmissionsWithPassedExpirationDate() {
            providerResponseRecurringJob.runNoProviderResponseJob();

            //Confirms that the method was called on the expired submission
            verify(ccmsSubmissionPayloadTransactionJob, times(1)).enqueueCCMSTransactionPayloadInstantly(
                    eq(activeWithPastExpirationDate.getId()));

            verify(sendEmailJob).enqueueSendEmailJob(any(ILGCCEmail.class));
        }
    }

    @Nested
    class whenThereAreMultipleProviders {

        @BeforeEach
        void setUp() {
            Map<String, Object> provider = new HashMap<>();
            provider.put("uuid", UUID.randomUUID().toString());
            provider.put("iterationIsComplete", true);
            provider.put("providerFirstName", "FirstName");
            provider.put("providerLastName", "LastName");
            provider.put("familyIntendedProviderEmail", "firstLast@mail.com");
            provider.put("familyIntendedProviderPhoneNumber", "(999) 123-1234");
            provider.put("familyIntendedProviderAddress", "123 Main St.");
            provider.put("familyIntendedProviderCity", "Chicago");
            provider.put("familyIntendedProviderState", "IL");
            provider.put("providerType", "Individual");

            Map<String, Object> child = new HashMap<>();
            child.put("uuid", UUID.randomUUID().toString());
            child.put("childFirstName", "First");
            child.put("childLastName", "Child");
            child.put("childInCare", "true");
            child.put("childDateOfBirthMonth", "10");
            child.put("childDateOfBirthDay", "11");
            child.put("childDateOfBirthYear", "2020");
            child.put("needFinancialAssistanceForChild", true);
            child.put("childIsUsCitizen", "Yes");
            child.put("ccapStartDate", "01/10/2025");

            activeWithFutureExpirationDate = new SubmissionTestBuilder()
                    .withParentDetails()
                    .with("parentContactEmail", "test-unexpired@mail.com")
                    .with("providerResponseStatus", SubmissionStatus.ACTIVE.name())
                    .with("providers", List.of(provider))
                    .with("children", List.of(child))
                    .withChildcareScheduleForProvider(child.get("uuid").toString(), provider.get("uuid").toString())
                    .with("providerApplicationResponseExpirationDate", OffsetDateTime.now().plusDays(3))
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withFlow("gcc")
                    .build();
            submissionRepository.save(activeWithFutureExpirationDate);

            OffsetDateTime expiredSubmissionDate = ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(OffsetDateTime.now())
                    .minusMinutes(5);
            activeWithPastExpirationDate = new SubmissionTestBuilder()
                    .withParentDetails()
                    .with("providers", List.of(provider))
                    .with("children", List.of(child))
                    .withChildcareScheduleForProvider(child.get("uuid").toString(), provider.get("uuid").toString())
                    .with("parentContactEmail", "test-expired@mail.com")
                    .with("providerResponseStatus", SubmissionStatus.ACTIVE.name())
                    .with("providerApplicationResponseExpirationDate", expiredSubmissionDate)
                    .withSubmittedAtDate(expiredSubmissionDate)
                    .withFlow("gcc")
                    .build();
            submissionRepository.save(activeWithPastExpirationDate);

            unsubmittedSubmission = new SubmissionTestBuilder()
                    .withParentDetails()
                    .with("parentContactEmail", "test-unsubmitted@mail.com")
                    .withFlow("gcc")
                    .build();
            submissionRepository.save(unsubmittedSubmission);
        }

        @AfterEach
        protected void clearSubmissions() {
            submissionRepository.deleteAll();
        }

        @ParameterizedTest
        @MethodSource("inactiveSubmissionStatuses")
        public void whenSubmissionHasExpirationDateInPast(SubmissionStatus status) {
            activeWithPastExpirationDate.getInputData().put("providerResponseStatus", status.name());
            submissionRepositoryService.save(activeWithPastExpirationDate);

            providerResponseRecurringJob.runNoProviderResponseJob();

            verify(ccmsSubmissionPayloadTransactionJob,
                    never()).enqueueCCMSTransactionPayloadInstantly(eq(activeWithPastExpirationDate.getId()));
            verify(sendEmailJob, never()).enqueueSendEmailJob(any(ILGCCEmail.class));

        }

        static Stream<Arguments> inactiveSubmissionStatuses() {
            return Stream.of(SubmissionStatus.values()).filter(s -> !s.name().equals(SubmissionStatus.ACTIVE.name()))
                    .map(status -> Arguments.of(status));
        }

        @Test
        void sendToCCMSIsOnlyCalledOnSubmissionsWithPassedExpirationDate() {
            providerResponseRecurringJob.runNoProviderResponseJob();

            //Confirms that the method was called on the expired submission
            verify(ccmsSubmissionPayloadTransactionJob, times(1)).enqueueCCMSTransactionPayloadInstantly(
                    eq(activeWithPastExpirationDate.getId()));

            verify(sendEmailJob).enqueueSendEmailJob(any(ILGCCEmail.class));
        }
    }

}
