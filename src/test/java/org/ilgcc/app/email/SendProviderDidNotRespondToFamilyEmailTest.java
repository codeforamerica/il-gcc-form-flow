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
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;


@ActiveProfiles("test")
@SpringBootTest
public class SendProviderDidNotRespondToFamilyEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    MessageSource messageSource;

    private Submission familySubmission;

    private SendProviderDidNotRespondToFamilyEmail sendEmailClass;

    private final Locale locale = Locale.ENGLISH;

    @BeforeEach
    void setUp() {
        familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentFirstName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .with("parentContactEmail", "familyemail@test.com")
                .with("languageRead", "English")
                .with("familyIntendedProviderName", "Intended Provider")
                .withSubmittedAtDate(OffsetDateTime.now())
                .withCCRR()
                .withShortCode("ABC123")
                .build());

        sendEmailClass = new SendProviderDidNotRespondToFamilyEmail(sendEmailJob, messageSource, submissionRepositoryService);
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
    void correctlySetsEmailData() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);

        assertThat(emailDataOptional.isPresent()).isTrue();

        Map<String, Object> emailData = emailDataOptional.get();

        assertThat(emailData.get("parentFirstName")).isEqualTo("FirstName");
        assertThat(emailData.get("parentContactEmail")).isEqualTo("familyemail@test.com");
        assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
        assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
        assertThat(emailData.get("confirmationCode")).isEqualTo("ABC123");
        assertThat(emailData.get("familyIntendedProviderName")).isEqualTo("Intended Provider");
    }

    @Test
    void correctlySetsEmailTemplateDataWhenProviderTypeIsNotSet() {
        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
        ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailDataOptional.get());

        assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
        assertThat(emailTemplate.getSubject()).isEqualTo(
                messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.subject", null, locale));

        String emailCopy = emailTemplate.getBody().getValue();

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p1", 
                        new Object[]{emailDataOptional.get().get("parentFirstName").toString()}, locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p2-individual",
                        new Object[]{emailDataOptional.get().get("familyIntendedProviderName").toString()},
                        locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p3",
                new Object[]{"ABC123"}, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p4",
                new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }

    @Test
    void correctlyUpdatesEmailSendStatus() {
        assertThat(familySubmission.getInputData().containsKey("providerResponseFamilyConfirmationEmailSent")).isFalse();
        sendEmailClass.send(familySubmission);

        assertThat(familySubmission.getInputData().containsKey("providerResponseFamilyConfirmationEmailSent")).isTrue();
        assertThat(familySubmission.getInputData().get("providerResponseFamilyConfirmationEmailSent")).isEqualTo("true");
    }

    @Test
    void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
        assertThat(sendEmailClass.skipEmailSend(familySubmission.getInputData())).isFalse();

        familySubmission.getInputData().put("providerResponseFamilyConfirmationEmailSent", "true");
        assertThat(sendEmailClass.skipEmailSend(familySubmission.getInputData())).isTrue();
    }

    @Test
    void correctlyEnqueuesSendEmailJob() {
        sendEmailClass.send(familySubmission);
        verify(sendEmailJob).enqueueSendEmailJob(any(ILGCCEmail.class), any(Integer.class));
    }

    @Nested
    class whenProviderTypeIsIndividual {

        private Submission familySubmissionIndividualProvider;
        Map<String, Object> provider;

        @BeforeEach
        void setUp() {

            provider = new HashMap<>();
            provider.put("uuid", "first-provider-uuid");
            provider.put("iterationIsComplete", true);
            provider.put("childCareProgramName", "FamilyChildCareName");
            provider.put("providerType", "Individual");
            provider.put("familyIntendedProviderEmail", "familyChildCareEmail");

            Map<String, Object> child1 = new HashMap<>();
            child1.put("uuid", "child-1-uuid");
            child1.put("childFirstName", "First");
            child1.put("childLastName", "Child");
            child1.put("childInCare", "true");
            child1.put("childDateOfBirthMonth", "10");
            child1.put("childDateOfBirthDay", "11");
            child1.put("childDateOfBirthYear", "2002");
            child1.put("needFinancialAssistanceForChild", true);
            child1.put("childIsUsCitizen", "Yes");
            child1.put("ccapStartDate", "01/10/2025");

            Map<String, Object> child2 = new HashMap<>();
            child2.put("uuid", "child-2-uuid");
            child2.put("childFirstName", "Second");
            child2.put("childLastName", "Child");
            child2.put("childInCare", "true");
            child2.put("childDateOfBirthMonth", "10");
            child2.put("childDateOfBirthDay", "11");
            child2.put("childDateOfBirthYear", "2002");
            child2.put("needFinancialAssistanceForChild", true);
            child2.put("childIsUsCitizen", "Yes");
            child2.put("ccapStartDate", "01/10/2025");

            familySubmissionIndividualProvider = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", List.of(provider))
                    .with("children", List.of(child1, child2))
                    .withChildcareScheduleForProvider("child-1-uuid", "first-provider-uuid")
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withCCRR()
                    .withShortCode("ABC123")
                    .build());
            sendEmailClass = new SendProviderDidNotRespondToFamilyEmail(sendEmailJob, messageSource, submissionRepositoryService);
        }

        @Test
        void correctlySetsEmailTemplateDataWhenProviderTypeIsIndividual() {
            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmissionIndividualProvider, provider);
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailDataOptional.get());

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.subject", null, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p1", new Object[]{"FirstName"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p2-individual",
                            new Object[]{emailDataOptional.get().get("childCareProviderInitials").toString()},
                            locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p3",
                    new Object[]{"ABC123"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p4",
                    new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                    locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }
    }

    @Nested
    class whenProviderTypeIsProgram {

        Map<String, Object> provider;

        private Submission familySubmissionProgramProvider;

        @BeforeEach
        void setUp() {

            provider = new HashMap<>();
            provider.put("uuid", "first-provider-uuid");
            provider.put("iterationIsComplete", true);
            provider.put("childCareProgramName", "FamilyChildCareName");
            provider.put("providerType", "Care Program");
            provider.put("familyIntendedProviderEmail", "familyChildCareEmail");

            Map<String, Object> child1 = new HashMap<>();
            child1.put("uuid", "child-1-uuid");
            child1.put("childFirstName", "First");
            child1.put("childLastName", "Child");
            child1.put("childInCare", "true");
            child1.put("childDateOfBirthMonth", "10");
            child1.put("childDateOfBirthDay", "11");
            child1.put("childDateOfBirthYear", "2002");
            child1.put("needFinancialAssistanceForChild", true);
            child1.put("childIsUsCitizen", "Yes");
            child1.put("ccapStartDate", "01/10/2025");

            Map<String, Object> child2 = new HashMap<>();
            child2.put("uuid", "child-2-uuid");
            child2.put("childFirstName", "Second");
            child2.put("childLastName", "Child");
            child2.put("childInCare", "true");
            child2.put("childDateOfBirthMonth", "10");
            child2.put("childDateOfBirthDay", "11");
            child2.put("childDateOfBirthYear", "2002");
            child2.put("needFinancialAssistanceForChild", true);
            child2.put("childIsUsCitizen", "Yes");
            child2.put("ccapStartDate", "01/10/2025");

            familySubmissionProgramProvider = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", List.of(provider))
                    .with("children", List.of(child1, child2))
                    .withChildcareScheduleForProvider("child-1-uuid", "first-provider-uuid")
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withCCRR()
                    .withShortCode("ABC123")
                    .build());
            sendEmailClass = new SendProviderDidNotRespondToFamilyEmail(sendEmailJob, messageSource, submissionRepositoryService);
        }

        @Test
        void correctlySetsEmailTemplateDataWhenProviderTypeIsProgram() {
            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmissionProgramProvider, provider);
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailDataOptional.get());

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.subject", null, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p1", new Object[]{"FirstName"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p2-program",
                            new Object[]{emailDataOptional.get().get("childCareProgramName").toString()},
                            locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p3",
                    new Object[]{"ABC123"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-did-not-respond.p4",
                    new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                    locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }
    }
}