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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@ActiveProfiles("test")
@TestPropertySource(properties = {"il-gcc.enable-multiple-providers=true"})
@SpringBootTest
public class SendFamilyApplicationTransmittedConfirmationEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    MessageSource messageSource;


    private SendFamilyApplicationTransmittedConfirmationEmail sendEmailClass;

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
    private static final String CONFIRMATION_CODE = "ABC123";

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
                .withShortCode(CONFIRMATION_CODE)
                .build());

        individualProviderSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .with("familySubmissionId", familySubmission.getId().toString())
                .with("currentProviderUuid", individualProvider.get("uuid").toString())
                .with("providerResponseContactEmail", "provideremail@test.com")
                .with("providerResponseFirstName", "ProviderFirst")
                .with("providerResponseLastName", "ProviderLast")
                .with("providerResponseAgreeToCare", "false")
                .build());

        individualProvider.put("providerResponseSubmissionId", individualProviderSubmission.getId());
        individualProvider.put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());

        programProviderSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .with("familySubmissionId", familySubmission.getId().toString())
                .with("currentProviderUuid", programProvider.get("uuid").toString())
                .with("providerResponseContactEmail", "programProviderEmail@test.com")
                .with("providerResponseBusinessName", "BusinessName")
                .with("providerCareStartDate", "05/15/2025")
                .with("providerResponseAgreeToCare", "true")
                .build());

        programProvider.put("providerResponseSubmissionId", programProviderSubmission.getId());
        programProvider.put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());

        sendEmailClass = new SendFamilyApplicationTransmittedConfirmationEmail(sendEmailJob, messageSource,
                submissionRepositoryService);

        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
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
        assertThat(emailData.get("confirmationCode")).isEqualTo(CONFIRMATION_CODE);
        assertThat(emailData.get("familyPreferredLanguage")).isEqualTo("English");
        assertThat(emailData.get("providersData")).isNotNull();

        // In this case, the providers data will be nested, so we need to check inside the next that it has been set correctly.
        List<Map<String, Object>> providersData = (List<Map<String, Object>>) emailData.get("providersData");
    }

    @Test
    void correctlySetsEmailDataForProviderWhoAcceptedCare() {
        assertThat(emailData.get("providersData")).isNotNull();

        List<Map<String, Object>> providersData = (List<Map<String, Object>>) emailData.get("providersData");
        // In this case, the providers data will be nested, so we need to check inside the next that it has been set correctly.

        Optional<Map<String, Object>> currentProviderDataOptional = providersData.stream()
                .filter(provider -> programProvider.get("uuid").equals(provider.get("uuid")))
                .findFirst();

        assertThat(currentProviderDataOptional.isPresent()).isTrue();
        Map<String, Object> currentProviderData = currentProviderDataOptional.get();

        assertThat(currentProviderData.get("ccapStartDate")).isEqualTo("May 15, 2025");
        assertThat(currentProviderData.get("childrenInitialsList")).isEqualTo(List.of("F.C."));
        assertThat(currentProviderData.get("providerName")).isEqualTo("BusinessName");
        assertThat(currentProviderData.get("providerResponseAgreeToCare")).isEqualTo("true");
        assertThat(currentProviderData.get("providerResponseContactEmail")).isEqualTo("programProviderEmail@test.com");
        assertThat(currentProviderData.get("providerType")).isEqualTo("Care Program");
    }

    @Test
    void correctlySetsEmailDataForProviderWhoDeclinedCare() {
        assertThat(emailData.get("providersData")).isNotNull();

        List<Map<String, Object>> providersData = (List<Map<String, Object>>) emailData.get("providersData");
        // In this case, the providers data will be nested, so we need to check inside the next that it has been set correctly.

        Optional<Map<String, Object>> currentProviderDataOptional = providersData.stream()
                .filter(provider -> individualProvider.get("uuid").equals(provider.get("uuid")))
                .findFirst();

        assertThat(currentProviderDataOptional.isPresent()).isTrue();
        Map<String, Object> currentProviderData = currentProviderDataOptional.get();

        assertThat(currentProviderData.get("ccapStartDate")).isEqualTo("January 10, 2025");
        assertThat(currentProviderData.get("childrenInitialsList")).isEqualTo(List.of("F.C.", "S.C."));
        assertThat(currentProviderData.get("providerType")).isEqualTo("Individual");
        assertThat(currentProviderData.get("childCareProviderInitials")).isEqualTo("F.L.");
        assertThat(currentProviderData.get("providerResponseAgreeToCare")).isEqualTo("false");
        assertThat(currentProviderData.get("providerResponseContactEmail")).isEqualTo("provideremail@test.com");
    }

    @Test
    void correctlySetsEmailDataForProviderWhoDoesNotRespond() {
        assertThat(emailData.get("providersData")).isNotNull();

        List<Map<String, Object>> providersData = (List<Map<String, Object>>) emailData.get("providersData");
        // In this case, the providers data will be nested, so we need to check inside the next that it has been set correctly.

        Optional<Map<String, Object>> currentProviderDataOptional = providersData.stream()
                .filter(provider -> noResponseProvider.get("uuid").equals(provider.get("uuid")))
                .findFirst();

        assertThat(currentProviderDataOptional.isPresent()).isTrue();
        Map<String, Object> currentProviderData = currentProviderDataOptional.get();

        assertThat(currentProviderData.get("ccapStartDate")).isEqualTo("December 10, 2025");
        assertThat(currentProviderData.get("childrenInitialsList")).isEqualTo(List.of("S.C."));
        assertThat(currentProviderData.get("providerType")).isEqualTo("Care Program");
        assertThat(currentProviderData.get("childCareProgramName")).isEqualTo("Great Care Child Care");
        assertThat(currentProviderData.get("providerResponseAgreeToCare")).isNull();
    }

    @Test
    void correctlySetsEmailTemplateDataWhenSomeRespond() {
        ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

        assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
        assertThat(emailTemplate.getSubject()).isEqualTo(
                messageSource.getMessage("email.family-application-transmitted-confirmation-email.subject", new Object[]{CONFIRMATION_CODE}, locale));

        String emailCopy = emailTemplate.getBody().getValue();

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.family-application-transmitted-confirmation-email.p1", new Object[]{"FirstName"},
                        locale));

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.family-application-transmitted-confirmation-email.p2.mixed-response",
                        new Object[]{"Sample Test CCRR"}, locale));

        assertThat(emailCopy).contains(messageSource.getMessage("email.family-application-transmitted-confirmation-email.p3",
                new Object[]{CONFIRMATION_CODE}, locale));

        assertThat(emailCopy).contains(messageSource.getMessage("email.family-application-transmitted-confirmation-email.p4",
                new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                locale));

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.family-application-transmitted-confirmation-email.li-agreed-to-care",
                        new Object[]{"BusinessName", "F.C.",
                                "May 15, 2025"}, locale));

        assertThat(emailCopy).contains(messageSource.getMessage(
                "email.family-application-transmitted-confirmation-email.li-did-not-agree-to-care",
                new Object[]{"F.L.", "F.C. and S.C."},
                locale));

        assertThat(emailCopy).contains(messageSource.getMessage(
                "email.family-application-transmitted-confirmation-email.li-did-not-complete-application-in-three-days",
                new Object[]{"Great Care Child Care"}, locale));

        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }

    @Test
    void correctlySetsEmailTemplateDataWhenAllRespond() {
        Submission noResponseSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .with("familySubmissionId", familySubmission.getId().toString())
                .with("currentProviderUuid", noResponseProvider.get("uuid").toString())
                .with("providerResponseContactEmail", "programProviderEmail@test.com")
                .with("providerResponseBusinessName", "Great Care Child Care")
                .with("providerCareStartDate", "06/30/2025")
                .with("providerResponseAgreeToCare", "true")
                .build());

        noResponseProvider.put("providerResponseSubmissionId", noResponseSubmission.getId());
        noResponseProvider.put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());

        sendEmailClass = new SendFamilyApplicationTransmittedConfirmationEmail(sendEmailJob, messageSource,
                submissionRepositoryService);

        Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(familySubmission);
        emailData = emailDataOptional.get();

        ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

        assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
        assertThat(emailTemplate.getSubject()).isEqualTo(
                messageSource.getMessage("email.family-application-transmitted-confirmation-email.subject", new Object[]{CONFIRMATION_CODE}, locale));

        String emailCopy = emailTemplate.getBody().getValue();

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.family-application-transmitted-confirmation-email.p1", new Object[]{"FirstName"},
                        locale));

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.family-application-transmitted-confirmation-email.p2.all-responded",
                        new Object[]{"Sample Test CCRR"}, locale));

        assertThat(emailCopy).contains(messageSource.getMessage("email.family-application-transmitted-confirmation-email.p3",
                new Object[]{CONFIRMATION_CODE}, locale));

        assertThat(emailCopy).contains(messageSource.getMessage("email.family-application-transmitted-confirmation-email.p4",
                new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                locale));

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.family-application-transmitted-confirmation-email.li-agreed-to-care",
                        new Object[]{"BusinessName", "F.C.",
                                "May 15, 2025"}, locale));

        assertThat(emailCopy).contains(messageSource.getMessage(
                "email.family-application-transmitted-confirmation-email.li-did-not-agree-to-care",
                new Object[]{"F.L.", "F.C. and S.C."},
                locale));

        assertThat(emailCopy).contains(messageSource.getMessage(
                "email.family-application-transmitted-confirmation-email.li-agreed-to-care",
                new Object[]{"Great Care Child Care", "S.C.",
                        "June 30, 2025"}, locale));

        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
        assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }

    @Test
    void correctlyUpdatesEmailSendStatus() {
        assertThat(familySubmission.getInputData().containsKey("familyApplicationTransmittedEmailSent")).isFalse();
        sendEmailClass.send(familySubmission);

        assertThat(familySubmission.getInputData().containsKey("familyApplicationTransmittedEmailSent")).isTrue();
        assertThat(familySubmission.getInputData().get("familyApplicationTransmittedEmailSent")).isEqualTo("true");
    }

    @Test
    void correctlySkipsEmailSendWhenEmailStatusIsTrue() {
        assertThat(sendEmailClass.skipEmailSend(familySubmission.getInputData())).isFalse();

        familySubmission.getInputData().put("familyApplicationTransmittedEmailSent", "true");
        assertThat(sendEmailClass.skipEmailSend(familySubmission.getInputData())).isTrue();
    }

    @Test
    void correctlyEnqueuesSendEmailJob() {
        sendEmailClass.send(familySubmission);
        verify(sendEmailJob).enqueueSendSubmissionEmailJob(any(ILGCCEmail.class), any(Integer.class));
    }
}