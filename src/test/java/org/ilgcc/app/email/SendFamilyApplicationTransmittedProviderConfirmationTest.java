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
import org.ilgcc.app.utils.enums.SubmissionStatus;
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
public class SendFamilyApplicationTransmittedProviderConfirmationTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    MessageSource messageSource;

    private Submission providerSubmission;

    private SendFamilyApplicationTransmittedProviderConfirmationEmail sendEmailClass;

    private final Locale locale = Locale.ENGLISH;

    private Submission familySubmission;

    private static Map<String, Object> individualProvider = new HashMap<>();

    private static Map<String, Object> programProvider = new HashMap<>();

    private static Map<String, Object> noResponseProvider = new HashMap<>();

    private Submission programProviderSubmission;

    private Submission individualProviderSubmission;
    private static Map<String, Object> child1 = new HashMap<>();

    private static Map<String, Object> child2 = new HashMap<>();

    private static Map<String, Object> emailData = new HashMap<>();

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
        individualProvider.put("providerApplicationResponseStatus", SubmissionStatus.ACTIVE.name());

        programProvider.put("uuid", UUID.randomUUID().toString());
        programProvider.put("iterationIsComplete", true);
        programProvider.put("childCareProgramName", "Child Care Program Name");
        programProvider.put("familyIntendedProviderEmail", "ccpn@mail.com");
        programProvider.put("familyIntendedProviderPhoneNumber", "(123) 123-1234");
        programProvider.put("familyIntendedProviderAddress", "456 Main St.");
        programProvider.put("familyIntendedProviderCity", "Chicago");
        programProvider.put("familyIntendedProviderState", "IL");
        programProvider.put("providerType", "Care Program");
        programProvider.put("providerApplicationResponseStatus", SubmissionStatus.ACTIVE.name());

        noResponseProvider.put("uuid", UUID.randomUUID().toString());
        noResponseProvider.put("iterationIsComplete", true);
        noResponseProvider.put("childCareProgramName", "Great Care Child Care");
        noResponseProvider.put("familyIntendedProviderEmail", "ccpn@mail.com");
        noResponseProvider.put("familyIntendedProviderPhoneNumber", "(123) 123-1234");
        noResponseProvider.put("familyIntendedProviderAddress", "456 Main St.");
        noResponseProvider.put("familyIntendedProviderCity", "Chicago");
        noResponseProvider.put("familyIntendedProviderState", "IL");
        noResponseProvider.put("providerType", "Care Program");
        noResponseProvider.put("providerApplicationResponseStatus", SubmissionStatus.ACTIVE.name());

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
    class whenSingleProvider {

        @BeforeEach
        void setUp() {
            Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentPreferredName", "FirstName").withChild("First", "Child", "true")
                    .withChild("Second", "Child", "true")
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .with("earliestChildcareStartDate", "01/10/2025")
                    .withCCRR()
                    .withShortCode("ABC123")
                    .build());

            providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("providerresponse")
                    .with("familySubmissionId", familySubmission.getId().toString())
                    .with("providerResponseContactEmail", "provideremail@test.com")
                    .with("providerResponseFirstName", "ProviderFirst")
                    .with("providerResponseLastName", "ProviderLast")
                    .with("providerResponseBusinessName", "BusinessName")
                    .with("providerResponseAgreeToCare", "true")
                    .build());

            sendEmailClass = new SendFamilyApplicationTransmittedProviderConfirmationEmail(sendEmailJob, messageSource,
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
            assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("provideremail@test.com");
        }

        @Test
        void correctlySetsEmailData() {
            assertThat(emailData.get("confirmationCode")).isEqualTo("ABC123");
            assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C.", "S.C."));
            assertThat(emailData.get("providerName")).isEqualTo("BusinessName");
            assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
            assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
            assertThat(emailData.get("ccapStartDate")).isEqualTo("January 10, 2025");
            assertThat(emailData.get("providerResponseContactEmail")).isEqualTo("provideremail@test.com");
        }

        @Test
        void correctlySetsEmailTemplateData() {
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.subject", new Object[]{"ABC123"}, locale)
            );

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.p1", null,
                            locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.p2",
                            new Object[]{"Sample Test CCRR"},
                            locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.p3",
                            new Object[]{"F.C. and S.C.", "January 10, 2025"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.p4",
                            new Object[]{"ABC123"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.p5",
                            new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                            locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }


        @Test
        void correctlyUpdatesEmailSendStatus() {
            assertThat(providerSubmission.getInputData().containsKey("applicationTransmittedConfirmationEmailSent")).isFalse();
            sendEmailClass.send(providerSubmission);

            assertThat(providerSubmission.getInputData().containsKey("applicationTransmittedConfirmationEmailSent")).isTrue();
            assertThat(providerSubmission.getInputData().get("applicationTransmittedConfirmationEmailSent")).isEqualTo("true");
        }

        @Test
        void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
            assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isFalse();

            providerSubmission.getInputData().put("applicationTransmittedConfirmationEmailSent", "true");
            assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isTrue();
        }

        @Test
        void correctlyEnqueuesSendEmailJob() {
            sendEmailClass.send(providerSubmission);
            verify(sendEmailJob).enqueueSendSubmissionEmailJob(any(ILGCCEmail.class), any(Integer.class));
        }
    }

    @Nested
    class whenMultipleProviders {

        @BeforeEach
        void setUp() {
            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", List.of(individualProvider, programProvider, noResponseProvider))
                    .with("children", List.of(child1, child2))
                    .withMultipleChildcareSchedules(List.of(child1.get("uuid").toString(), child2.get("uuid").toString(),
                                    child1.get("uuid").toString(), child2.get("uuid").toString()),
                            List.of(individualProvider.get("uuid").toString(),
                                    individualProvider.get("uuid").toString(), programProvider.get("uuid").toString(),
                                    noResponseProvider.get("uuid").toString()))
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withCCRR()
                    .withShortCode("ABC123")
                    .build());

            providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("providerresponse")
                    .with("familySubmissionId", familySubmission.getId().toString())
                    .with("currentProviderUuid", programProvider.get("uuid"))
                    .with("providerResponseContactEmail", "provideremail@test.com")
                    .with("providerResponseFirstName", "ProviderFirst")
                    .with("providerResponseLastName", "ProviderLast")
                    .with("providerResponseBusinessName", "BusinessName")
                    .with("providerResponseAgreeToCare", "true")
                    .build());

            sendEmailClass = new SendFamilyApplicationTransmittedProviderConfirmationEmail(sendEmailJob, messageSource,
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
            assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("provideremail@test.com");
        }

        @Test
        void correctlySetsEmailData() {
            assertThat(emailData.get("confirmationCode")).isEqualTo("ABC123");
            assertThat(emailData.get("childrenInitialsList")).isEqualTo(List.of("F.C."));
            assertThat(emailData.get("providerName")).isEqualTo("BusinessName");
            assertThat(emailData.get("ccrrName")).isEqualTo("Sample Test CCRR");
            assertThat(emailData.get("ccrrPhoneNumber")).isEqualTo("(603) 555-1244");
            assertThat(emailData.get("ccapStartDate")).isEqualTo("January 10, 2025");
            assertThat(emailData.get("providerResponseContactEmail")).isEqualTo("provideremail@test.com");
        }

        @Test
        void correctlySetsEmailTemplateData() {
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.subject", new Object[]{"ABC123"}, locale)
            );

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.p1", null,
                            locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.p2",
                            new Object[]{"Sample Test CCRR"},
                            locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.p3",
                            new Object[]{"F.C.", "January 10, 2025"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.p4",
                            new Object[]{"ABC123"}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.family-application-transmitted-provider-confirmation-email.p5",
                            new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                            locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }


        @Test
        void correctlyUpdatesEmailSendStatus() {
            assertThat(providerSubmission.getInputData().containsKey("applicationTransmittedConfirmationEmailSent")).isFalse();
            sendEmailClass.send(providerSubmission);

            assertThat(providerSubmission.getInputData().containsKey("applicationTransmittedConfirmationEmailSent")).isTrue();
            assertThat(providerSubmission.getInputData().get("applicationTransmittedConfirmationEmailSent")).isEqualTo("true");
        }

        @Test
        void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
            assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isFalse();

            providerSubmission.getInputData().put("applicationTransmittedConfirmationEmailSent", "true");
            assertThat(sendEmailClass.skipEmailSend(providerSubmission.getInputData())).isTrue();
        }

        @Test
        void correctlyEnqueuesSendEmailJob() {
            sendEmailClass.send(providerSubmission);
            verify(sendEmailJob).enqueueSendSubmissionEmailJob(any(ILGCCEmail.class), any(Integer.class));
        }
    }

}