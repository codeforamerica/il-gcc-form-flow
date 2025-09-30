package org.ilgcc.app.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.email.ILGCCEmail.FROM_ADDRESS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import com.sendgrid.helpers.mail.objects.Email;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.jobs.SendEmailJob;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;


@ActiveProfiles("test")
@SpringBootTest
public class SendFamilyConfirmationEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    MessageSource messageSource;

    private Submission familySubmission;

    private SendFamilyConfirmationEmail sendEmailClass;

    Map<String, Object> programProvider = new HashMap<>();
    Map<String, Object> individualProvider = new HashMap<>();

    Map<String, Object> child1 = new HashMap<>();
    Map<String, Object> child2 = new HashMap<>();

    Map<String, Object> emailData;

    private final Locale locale = Locale.ENGLISH;

    private static final String CONFIRMATION_CODE = "ABC123";

    @BeforeEach
    void setUp() {
        familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentFirstName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .withSubmittedAtDate(OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)))
                .withCCRR()
                .with("parentContactEmail", "familyemail@test.com")
                .with("shareableLink", "tempEmailLink")
                .withShortCode(CONFIRMATION_CODE)
                .build());

        sendEmailClass = new SendFamilyConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @AfterEach
    void tearDown() {
        submissionRepository.deleteAll();
    }

    @Test
    void correctlySetsEmailRecipient() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("familyemail@test.com");
    }

    @Test
    void correctlyUpdatesEmailSendStatus() {
        assertThat(familySubmission.getInputData().containsKey("familyConfirmationEmailSent")).isFalse();
        sendEmailClass.send(familySubmission);

        assertThat(familySubmission.getInputData().containsKey("familyConfirmationEmailSent")).isTrue();
        assertThat(familySubmission.getInputData().get("familyConfirmationEmailSent")).isEqualTo("true");
    }

    @Test
    void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
        assertThat(sendEmailClass.skipEmailSend(familySubmission.getInputData())).isFalse();

        familySubmission.getInputData().put("familyConfirmationEmailSent", "true");
        assertThat(sendEmailClass.skipEmailSend(familySubmission.getInputData())).isTrue();
    }

    @Test
    void correctlyEnqueuesSendEmailJob() {
        sendEmailClass.send(familySubmission);
        verify(sendEmailJob).enqueueSendSubmissionEmailJob(any(ILGCCEmail.class), any(Integer.class));
    }

    @Nested
    class whenThereIsASingleProvider {

        @Test
        void correctlySetsEmailData() {
            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);

            assertThat(emailDataOptional.isPresent()).isTrue();

            Map<String, Object> emailData = emailDataOptional.get();

            assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
            assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
            assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
            assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
            assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C.", "S.C."));
            assertThat(emailData.get("confirmationCode")).isEqualTo(CONFIRMATION_CODE);
            assertThat(emailData.get("submittedDate")).isEqualTo("October 10, 2022");
            assertThat(emailData.get("shareableLink")).isEqualTo("tempEmailLink");
            assertThat(emailData.get("hasMultipleProviders")).isEqualTo(false);
        }

        @Test
        void correctlySetsEmailTemplateData() {
            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailDataOptional.get());

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.family-confirmation.subject", new Object[]{CONFIRMATION_CODE}, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p1", new Object[]{"FirstName"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p2", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p3.single-provider", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p4.single-provider", new Object[]{"tempEmailLink"},
                            locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p5", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p6",
                    new Object[]{"Sample Test CCRR", "(603) 555-1244"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p7",
                    new Object[]{CONFIRMATION_CODE, "October 10, 2022"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p8", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p9", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p10", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }

    }

    @Nested
    class whenThereAreMultipleProviders {

        @BeforeEach
        void setUp() {
            programProvider.put("uuid", UUID.randomUUID().toString());
            programProvider.put("iterationIsComplete", true);
            programProvider.put("childCareProgramName", "Child Care Program Name");
            programProvider.put("familyIntendedProviderEmail", "ccpn@mail.com");
            programProvider.put("familyIntendedProviderPhoneNumber", "(123) 123-1234");
            programProvider.put("familyIntendedProviderAddress", "456 Main St.");
            programProvider.put("familyIntendedProviderCity", "Chicago");
            programProvider.put("familyIntendedProviderState", "IL");
            programProvider.put("providerType", "Care Program");

            individualProvider.put("uuid", UUID.randomUUID().toString());
            individualProvider.put("iterationIsComplete", true);
            individualProvider.put("providerFirstName", "FirstName");
            individualProvider.put("providerLastName", "LastName");
            individualProvider.put("familyIntendedProviderEmail", "firstLast@mail.com");
            individualProvider.put("familyIntendedProviderPhoneNumber", "(999) 123-1234");
            individualProvider.put("familyIntendedProviderAddress", "123 Main St.");
            individualProvider.put("familyIntendedProviderCity", "Chicago");
            individualProvider.put("familyIntendedProviderState", "IL");
            individualProvider.put("providerType", "Individual");

            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                    .withSubmittedAtDate(OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)))
                    .withCCRR()
                    .with("providers", List.of(individualProvider, programProvider))
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("shareableLink", "tempEmailLink")
                    .withShortCode(CONFIRMATION_CODE)
                    .build());

            sendEmailClass = new SendFamilyConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
            emailData = emailDataOptional.get();
        }

        @Test
        void correctlySetsEmailData() {
            assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
            assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
            assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
            assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
            assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C.", "S.C."));
            assertThat(emailData.get("confirmationCode")).isEqualTo(CONFIRMATION_CODE);
            assertThat(emailData.get("submittedDate")).isEqualTo("October 10, 2022");
            assertThat(emailData.get("shareableLink")).isEqualTo("tempEmailLink");
            assertThat(emailData.get("hasMultipleProviders")).isEqualTo(true);
        }

        @Test
        void correctlySetsEmailTemplateData() {
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.family-confirmation.subject", new Object[]{CONFIRMATION_CODE}, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p1", new Object[]{"FirstName"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p2", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p3.multiple-providers", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p4.multiple-providers", new Object[]{"tempEmailLink"},
                            locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p5", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p6",
                    new Object[]{"Sample Test CCRR", "(603) 555-1244"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p7",
                    new Object[]{CONFIRMATION_CODE, "October 10, 2022"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p8", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p9", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p10", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }

    }

    @Nested
    class whenThereIsSingleProviderInMultipleProviders {

        @BeforeEach
        void setUp() {
            individualProvider.put("uuid", UUID.randomUUID().toString());
            individualProvider.put("iterationIsComplete", true);
            individualProvider.put("providerFirstName", "FirstName");
            individualProvider.put("providerLastName", "LastName");
            individualProvider.put("familyIntendedProviderEmail", "firstLast@mail.com");
            individualProvider.put("familyIntendedProviderPhoneNumber", "(999) 123-1234");
            individualProvider.put("familyIntendedProviderAddress", "123 Main St.");
            individualProvider.put("familyIntendedProviderCity", "Chicago");
            individualProvider.put("familyIntendedProviderState", "IL");
            individualProvider.put("providerType", "Individual");

            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                    .withSubmittedAtDate(OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)))
                    .withCCRR()
                    .with("providers", List.of(individualProvider))
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("shareableLink", "tempEmailLink")
                    .withShortCode(CONFIRMATION_CODE)
                    .build());

            sendEmailClass = new SendFamilyConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
            emailData = emailDataOptional.get();
        }

        @Test
        void correctlySetsEmailData() {
            assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
            assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
            assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
            assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
            assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C.", "S.C."));
            assertThat(emailData.get("confirmationCode")).isEqualTo(CONFIRMATION_CODE);
            assertThat(emailData.get("submittedDate")).isEqualTo("October 10, 2022");
            assertThat(emailData.get("shareableLink")).isEqualTo("tempEmailLink");
            assertThat(emailData.get("hasMultipleProviders")).isEqualTo(false);
        }

        @Test
        void correctlySetsEmailTemplateData() {
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.family-confirmation.subject", new Object[]{CONFIRMATION_CODE}, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p1", new Object[]{"FirstName"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p2", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p3.single-provider", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p4.single-provider", new Object[]{"tempEmailLink"},
                            locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p5", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p6",
                    new Object[]{"Sample Test CCRR", "(603) 555-1244"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p7",
                    new Object[]{CONFIRMATION_CODE, "October 10, 2022"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p8", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p9", null, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-confirmation.p10", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }

        @Nested
        class whenThereIsAProviderAndNoProviderInMultipleProviders {

            @BeforeEach
            void setUp() {

                child1.put("uuid", UUID.randomUUID().toString());
                child1.put("childFirstName", "First");
                child1.put("childLastName", "Child");
                child1.put("childInCare", "true");
                child1.put("childDateOfBirthMonth", "10");
                child1.put("childDateOfBirthDay", "11");
                child1.put("childDateOfBirthYear", "2020");
                child1.put("needFinancialAssistanceForChild", true);
                child1.put("childIsUsCitizen", "Yes");
                child1.put("ccapStartDate", "01/10/2025");

                child2.put("uuid", UUID.randomUUID().toString());
                child2.put("childFirstName", "Second");
                child2.put("childLastName", "Child");
                child2.put("childInCare", "true");
                child2.put("childDateOfBirthMonth", "12");
                child2.put("childDateOfBirthDay", "11");
                child2.put("childDateOfBirthYear", "2021");
                child2.put("needFinancialAssistanceForChild", true);
                child2.put("childIsUsCitizen", "Yes");
                child2.put("ccapStartDate", "12/10/2025");

                programProvider.put("uuid", UUID.randomUUID().toString());
                programProvider.put("iterationIsComplete", true);
                programProvider.put("childCareProgramName", "Child Care Program Name");
                programProvider.put("familyIntendedProviderEmail", "ccpn@mail.com");
                programProvider.put("familyIntendedProviderPhoneNumber", "(123) 123-1234");
                programProvider.put("familyIntendedProviderAddress", "456 Main St.");
                programProvider.put("familyIntendedProviderCity", "Chicago");
                programProvider.put("familyIntendedProviderState", "IL");

                familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                        .withFlow("gcc")
                        .with("parentFirstName", "FirstName")
                        .withSubmittedAtDate(OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)))
                        .withCCRR()
                        .with("providers", List.of(individualProvider, programProvider))
                        .with("children", List.of(child1, child2))
                        .withMultipleChildcareSchedules(
                                List.of(child1.get("uuid").toString(), child2.get("uuid").toString(),
                                        child1.get("uuid").toString()),
                                List.of(programProvider.get("uuid").toString(), "NO_PROVIDER", individualProvider.get("uuid").toString()))
                        .with("parentContactEmail", "familyemail@test.com")
                        .with("shareableLink", "tempEmailLink")
                        .withShortCode(CONFIRMATION_CODE)
                        .build());

                Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
                emailData = emailDataOptional.get();
            }


            @Test
            void correctlySetsEmailData() {
                assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
                assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
                assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
                assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
                assertThat(emailData.get("confirmationCode")).isEqualTo(CONFIRMATION_CODE);
                assertThat(emailData.get("submittedDate")).isEqualTo("October 10, 2022");
                assertThat(emailData.get("shareableLink")).isEqualTo("tempEmailLink");
                assertThat(emailData.get("hasMultipleProviders")).isEqualTo(true);
                assertThat(emailData.get("hasProviderAndNoProvider")).isEqualTo(true);
            }

            @Test
            void correctlySetsEmailTemplateData() {
                ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

                assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                        new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
                assertThat(emailTemplate.getSubject()).isEqualTo(
                        messageSource.getMessage("email.family-confirmation.subject", new Object[]{CONFIRMATION_CODE}, locale));

                String emailCopy = emailTemplate.getBody().getValue();

                assertThat(emailCopy).contains(
                        messageSource.getMessage("email.family-confirmation.p1", new Object[]{"FirstName"}, locale));
                assertThat(emailCopy).contains(
                        messageSource.getMessage("email.family-confirmation.p2", null, locale));
                assertThat(emailCopy).contains(
                        messageSource.getMessage("email.family-confirmation.p3.multiple-providers", null, locale));
                assertThat(emailCopy).contains(
                        messageSource.getMessage("email.family-confirmation.p4.multiple-providers", new Object[]{"tempEmailLink"},
                                locale));
                assertThat(emailCopy).contains(
                        messageSource.getMessage("email.family-confirmation.some-providers.did-not-choose",
                                new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                                locale));
                assertThat(emailCopy).contains(
                        messageSource.getMessage("email.family-confirmation.p5", null, locale));
                assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p6",
                        new Object[]{"Sample Test CCRR", "(603) 555-1244"}, locale));
                assertThat(emailCopy).contains(messageSource.getMessage("email.family-confirmation.p7",
                        new Object[]{CONFIRMATION_CODE, "October 10, 2022"}, locale));
                assertThat(emailCopy).contains(
                        messageSource.getMessage("email.family-confirmation.p8", null, locale));
                assertThat(emailCopy).contains(
                        messageSource.getMessage("email.family-confirmation.p9", null, locale));
                assertThat(emailCopy).contains(
                        messageSource.getMessage("email.family-confirmation.p10", null, locale));
                assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
                assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
            }
        }
    }

}