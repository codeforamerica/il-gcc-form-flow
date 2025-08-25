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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.jobs.SendEmailJob;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;


@ActiveProfiles("test")
@SpringBootTest
public class SendProviderDeclinesCareFamilyConfirmationEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    MessageSource messageSource;

    private Submission providerSubmission;

    private SendProviderDeclinesCareFamilyConfirmationEmail sendEmailClass;

    private final Locale locale = Locale.ENGLISH;

    private Submission familySubmission;

    private static Map<String, Object> individualProvider = new HashMap<>();

    private static Map<String, Object> programProvider = new HashMap<>();

    private static Map<String, Object> child1 = new HashMap<>();

    private static Map<String, Object> child2 = new HashMap<>();

    private static final String CONFIRMATION_CODE = "ABC123";

    @BeforeAll
    public static void setUpOnce() {
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

        programProvider.put("uuid", UUID.randomUUID().toString());
        programProvider.put("iterationIsComplete", true);
        programProvider.put("childCareProgramName", "Child Care Program Name");
        programProvider.put("familyIntendedProviderEmail", "ccpn@mail.com");
        programProvider.put("familyIntendedProviderPhoneNumber", "(123) 123-1234");
        programProvider.put("familyIntendedProviderAddress", "456 Main St.");
        programProvider.put("familyIntendedProviderCity", "Chicago");
        programProvider.put("familyIntendedProviderState", "IL");
        programProvider.put("providerType", "Care Program");

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
    }

    @Nested
    class whenProviderTypeIsNotSet {

        Map<String, Object> emailData;

        @BeforeEach
        void setUp() {
            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("familyIntendedProviderName", "BusinessName")
                    .with("languageRead", "English")
                    .with("earliestChildcareStartDate", "01/10/2025")
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withCCRR()
                    .withShortCode(CONFIRMATION_CODE)
                    .build());

            providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("providerresponse")
                    .with("familySubmissionId", familySubmission.getId().toString())
                    .with("providerResponseFirstName", "ProviderFirst")
                    .with("providerResponseLastName", "ProviderLast")
                    .with("providerResponseBusinessName", "BusinessName")
                    .with("providerCareStartDate", "06/16/2025")
                    .with("providerResponseAgreeToCare", "false")
                    .build());

            sendEmailClass = new SendProviderDeclinesCareFamilyConfirmationEmail(sendEmailJob, messageSource,
                    submissionRepositoryService);

            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
            emailData = emailDataOptional.get();
        }

        @AfterEach
        void tearDown() {
            submissionRepository.deleteAll();
        }

        @Test
        void correctlySetsEmailRecipient() {
            assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("familyemail@test.com");
        }

        @Test
        void correctlySetsEmailData() {
            assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
            assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
            assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
            assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
            assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C.", "S.C."));
            assertThat(emailData.get("confirmationCode")).isEqualTo(CONFIRMATION_CODE);
            assertThat(emailData.get("familyIntendedProviderName")).isEqualTo("BusinessName");
            assertThat(emailData.get("ccapStartDate")).isEqualTo("June 16, 2025");
            assertThat(emailData.get("familyPreferredLanguage")).isEqualTo("English");

            assertThat(emailData.get("providerType")).isEqualTo("");
            assertThat(emailData.get("childCareProgramName")).isEqualTo("BusinessName");
            assertThat(emailData.get("childCareProviderInitials")).isEqualTo("P.P.");
        }

        @Test
        void correctlySetsEmailTemplateData() {
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.response-email-for-family.provider-declines.subject", new Object[]{CONFIRMATION_CODE}, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p1",
                    new Object[]{"FirstName"},
                    locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-declines.p2-program",
                            new Object[]{"BusinessName", "Sample Test CCRR"},
                            locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p3",
                    new Object[]{"F.C. and S.C.", "June 16, 2025"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p4",
                    new Object[]{CONFIRMATION_CODE}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p5",
                    new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                    locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-declines.p6", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }

        @Test
        void correctlyUpdatesEmailSendStatus() {
            assertThat(providerSubmission.getInputData().containsKey("providerResponseFamilyConfirmationEmailSent")).isFalse();
            sendEmailClass.send(providerSubmission);

            assertThat(providerSubmission.getInputData().containsKey("providerResponseFamilyConfirmationEmailSent")).isTrue();
            assertThat(providerSubmission.getInputData().get("providerResponseFamilyConfirmationEmailSent")).isEqualTo("true");
        }

        @Test
        void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
            assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isFalse();

            providerSubmission.getInputData().put("providerResponseFamilyConfirmationEmailSent", "true");
            assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isTrue();
        }

        @Test
        void correctlyEnqueuesSendEmailJob() {
            sendEmailClass.send(providerSubmission);
            verify(sendEmailJob).enqueueSendSubmissionEmailJob(any(ILGCCEmail.class), any(Integer.class));
        }

    }

    @Nested
    class whenProviderIsAnIndividual {

        Map<String, Object> emailData;

        @BeforeEach
        void setUp() {
            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", List.of(individualProvider))
                    .with("children", List.of(child1, child2))
                    .withChildcareScheduleForProvider(child1.get("uuid").toString(), individualProvider.get("uuid").toString())
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withCCRR()
                    .withShortCode(CONFIRMATION_CODE)
                    .build());

            providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("providerresponse")
                    .with("familySubmissionId", familySubmission.getId().toString())
                    .with("currentProviderUuid", individualProvider.get("uuid").toString())
                    .with("providerResponseContactEmail", "provideremail@test.com")
                    .with("providerResponseFirstName", "ProviderFirst")
                    .with("providerResponseLastName", "ProviderLast")
                    .with("providerCareStartDate", "05/10/2025")
                    .with("providerResponseAgreeToCare", "false")
                    .build());

            individualProvider.put("providerResponseSubmissionId", providerSubmission.getId());

            sendEmailClass = new SendProviderDeclinesCareFamilyConfirmationEmail(sendEmailJob, messageSource,
                    submissionRepositoryService);


            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
            emailData = emailDataOptional.get();
        }

        @AfterEach
        void tearDown() {
            submissionRepository.deleteAll();
        }


        @Test
        void correctlySetsEmailRecipient() {
            assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("familyemail@test.com");
        }

        @Test
        void correctlySetsEmailData() {
            assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
            assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
            assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
            assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
            assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C."));
            assertThat(emailData.get("confirmationCode")).isEqualTo(CONFIRMATION_CODE);

            assertThat(emailData.get("providerName")).isEqualTo("ProviderFirst");
            assertThat(emailData.get("ccapStartDate")).isEqualTo("May 10, 2025");
            assertThat(emailData.get("familyPreferredLanguage")).isEqualTo("English");

            assertThat(emailData.get("providerType")).isEqualTo("Individual");
            assertThat(emailData.get("childCareProgramName")).isEqualTo("");
            assertThat(emailData.get("childCareProviderInitials")).isEqualTo("P.P.");
        }

        @Test
        void correctlySetsEmailTemplateData() {
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.response-email-for-family.provider-declines.subject", new Object[]{CONFIRMATION_CODE}, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p1",
                    new Object[]{"FirstName"},
                    locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-declines.p2-individual",
                            new Object[]{"P.P.", "Sample Test CCRR"},
                            locale));

            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p3",
                    new Object[]{"F.C.", "May 10, 2025"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p4",
                    new Object[]{CONFIRMATION_CODE}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p5",
                    new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                    locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-declines.p6", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }

        @Test
        void correctlyUpdatesEmailSendStatus() {
            assertThat(providerSubmission.getInputData().containsKey("providerResponseFamilyConfirmationEmailSent")).isFalse();
            sendEmailClass.send(providerSubmission);

            assertThat(providerSubmission.getInputData().containsKey("providerResponseFamilyConfirmationEmailSent")).isTrue();
            assertThat(providerSubmission.getInputData().get("providerResponseFamilyConfirmationEmailSent")).isEqualTo("true");
        }

        @Test
        void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
            assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isFalse();

            providerSubmission.getInputData().put("providerResponseFamilyConfirmationEmailSent", "true");
            assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isTrue();
        }

        @Test
        void correctlyEnqueuesSendEmailJob() {
            sendEmailClass.send(providerSubmission);
            verify(sendEmailJob).enqueueSendSubmissionEmailJob(any(ILGCCEmail.class), any(Integer.class));
        }
    }

    @Nested
    class whenProviderIsAProgram {
        Map<String, Object> emailData;

        @BeforeEach
        void setUp() {
            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", List.of(programProvider))
                    .with("children", List.of(child1, child2))
                    .withChildcareScheduleForProvider(child2.get("uuid").toString(), programProvider.get("uuid").toString())
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withCCRR()
                    .withShortCode(CONFIRMATION_CODE)
                    .build());

            providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("providerresponse")
                    .with("currentProviderUuid", programProvider.get("uuid").toString())
                    .with("familySubmissionId", familySubmission.getId().toString())
                    .with("providerResponseContactEmail", "provideremail@test.com")
                    .with("providerResponseBusinessName", "BusinessName")
                    .with("providerResponseFirstName", "ProviderFirst")
                    .with("providerResponseLastName", "ProviderLast")
                    .with("providerCareStartDate", "01/10/2025")
                    .with("providerResponseAgreeToCare", "false")
                    .build());

            sendEmailClass = new SendProviderDeclinesCareFamilyConfirmationEmail(sendEmailJob, messageSource,
                    submissionRepositoryService);

            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
            emailData = emailDataOptional.get();
        }

        @AfterEach
        void tearDown() {
            submissionRepository.deleteAll();
        }


        @Test
        void correctlySetsEmailRecipient() {
            assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("familyemail@test.com");
        }

        @Test
        void correctlySetsEmailData() {
            assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
            assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
            assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
            assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
            assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("S.C."));
            assertThat(emailData.get("confirmationCode")).isEqualTo(CONFIRMATION_CODE);
            assertThat(emailData.get("providerName")).isEqualTo("BusinessName");
            assertThat(emailData.get("ccapStartDate")).isEqualTo("January 10, 2025");
            assertThat(emailData.get("familyPreferredLanguage")).isEqualTo("English");

            assertThat(emailData.get("providerType")).isEqualTo("Care Program");
            assertThat(emailData.get("childCareProgramName")).isEqualTo("BusinessName");
            assertThat(emailData.get("childCareProviderInitials")).isEqualTo("P.P.");
        }

        @Test
        void correctlySetsEmailTemplateData() {
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.response-email-for-family.provider-declines.subject", new Object[]{CONFIRMATION_CODE}, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p1",
                    new Object[]{"FirstName"},
                    locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-declines.p2-program",
                            new Object[]{"BusinessName", "Sample Test CCRR"},
                            locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p3",
                    new Object[]{"S.C.", "January 10, 2025"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p4",
                    new Object[]{CONFIRMATION_CODE}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-declines.p5",
                    new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                    locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-declines.p6", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }

        @Test
        void correctlyUpdatesEmailSendStatus() {
            assertThat(providerSubmission.getInputData().containsKey("providerResponseFamilyConfirmationEmailSent")).isFalse();
            sendEmailClass.send(providerSubmission);

            assertThat(providerSubmission.getInputData().containsKey("providerResponseFamilyConfirmationEmailSent")).isTrue();
            assertThat(providerSubmission.getInputData().get("providerResponseFamilyConfirmationEmailSent")).isEqualTo("true");
        }

        @Test
        void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
            assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isFalse();

            providerSubmission.getInputData().put("providerResponseFamilyConfirmationEmailSent", "true");
            assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isTrue();
        }

        @Test
        void correctlyEnqueuesSendEmailJob() {
            sendEmailClass.send(providerSubmission);
            verify(sendEmailJob).enqueueSendSubmissionEmailJob(any(ILGCCEmail.class), any(Integer.class));
        }
    }

}