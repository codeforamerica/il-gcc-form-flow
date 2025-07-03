package org.ilgcc.app.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.email.ILGCCEmail.FROM_ADDRESS;

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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;


@ActiveProfiles("test")
@SpringBootTest
public class SendNewProviderAgreesToCareFamilyConfirmationEmailTest {

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    MessageSource messageSource;

    private Submission providerSubmission;

    private SendNewProviderAgreesToCareFamilyConfirmationEmail sendEmailClass;

    private final Locale locale = Locale.ENGLISH;


    @BeforeEach
    void setUp() {
        Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentFirstName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .with("parentContactEmail", "familyemail@test.com")
                .with("languageRead", "English")
                .withSubmittedAtDate(OffsetDateTime.now())
                .withCCRR()
                .withShortCode("ABC123")
                .build());

        providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .with("familySubmissionId", familySubmission.getId().toString())
                .with("providerResponseContactEmail", "provideremail@test.com")
                .with("providerResponseFirstName", "ProviderFirst")
                .with("providerResponseLastName", "ProviderLast")
                .with("providerCareStartDate", "01/10/2025")
                .with("providerResponseAgreeToCare", "true")
                .build());

        sendEmailClass = new SendNewProviderAgreesToCareFamilyConfirmationEmail(sendEmailJob, messageSource,
                submissionRepositoryService);
    }

    @AfterEach
    void tearDown() {
        submissionRepository.deleteAll();
    }
    
    @Nested
    class whenProviderDoesNotEnterBusinessName {
        Map<String, Object> provider;
        
        @BeforeEach
        void setUp() {
            provider = new HashMap<>();
            provider.put("uuid", "first-provider-uuid");
            provider.put("iterationIsComplete", true);
            provider.put("familyIntendedProviderEmail", "familyChildCareEmail");

            Map<String, Object> child1 = new HashMap<>();
            child1.put("uuid", "child-1-uuid");
            child1.put("childFirstName", "First");
            child1.put("childLastName", "Child");
            child1.put("childInCare", "true");
            child1.put("childDateOfBirthMonth", "10");
            child1.put("childDateOfBirthDay", "11");
            child1.put("childDateOfBirthYear", "2002");
            child1.put("needFinancialAssistanceForChild", "true");
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
            child2.put("needFinancialAssistanceForChild", "true");
            child2.put("childIsUsCitizen", "Yes");
            child2.put("ccapStartDate", "01/10/2025");
            
            Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", List.of(provider))
                    .with("children", List.of(child1, child2))
                    .withMultipleChildcareSchedulesForProvider(List.of(child1.get("uuid").toString(), child2.get("uuid").toString()), provider.get("uuid").toString())
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withCCRR()
                    .withShortCode("ABC123")
                    .build());

            providerSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("providerresponse")
                    .with("familySubmissionId", familySubmission.getId().toString())
                    .with("providerResponseContactEmail", "provideremail@test.com")
                    .with("providerResponseFirstName", "ProviderFirst")
                    .with("providerResponseLastName", "ProviderLast")
                    .with("providerCareStartDate", "01/10/2025")
                    .with("providerResponseAgreeToCare", "true")
                    .with("currentProviderUuid", provider.get("uuid"))
                    .build());

            provider.put("providerResponseSubmissionId", providerSubmission.getId());

            sendEmailClass = new SendNewProviderAgreesToCareFamilyConfirmationEmail(sendEmailJob, messageSource,
                    submissionRepositoryService);
        }

        @AfterEach
        void tearDown() {
            submissionRepository.deleteAll();
        }

        @Test
        void correctlySetsEmailTemplateData() {
            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission, provider);
            assertThat(emailDataOptional.isPresent()).isTrue();
            Map<String, Object> emailData = emailDataOptional.get();
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.response-email-for-family.provider-agrees.subject", null, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-agrees.p1",
                            new Object[]{emailData.get("parentFirstName")}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-agrees.p2-individual",
                            new Object[]{emailData.get("childCareProviderInitials")},
                            locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p3",
                    new Object[]{"F.C. and S.C.", "January 10, 2025"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p4",
                    new Object[]{"ABC123"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p5",
                    new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                    locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.new-provider-agrees.note", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }
    }

    @Nested
    class whenProviderEntersABusinessName {
        Map<String, Object> provider;

        @BeforeEach
        void setUp() {
            provider = new HashMap<>();
            provider.put("uuid", "first-provider-uuid");
            provider.put("iterationIsComplete", true);
//            provider.put("childCareProgramName", "FamilyChildCareName");
            provider.put("familyIntendedProviderEmail", "familyChildCareEmail");

            Map<String, Object> child1 = new HashMap<>();
            child1.put("uuid", "child-1-uuid");
            child1.put("childFirstName", "First");
            child1.put("childLastName", "Child");
            child1.put("childInCare", "true");
            child1.put("childDateOfBirthMonth", "10");
            child1.put("childDateOfBirthDay", "11");
            child1.put("childDateOfBirthYear", "2002");
            child1.put("needFinancialAssistanceForChild", "true");
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
            child2.put("needFinancialAssistanceForChild", "true");
            child2.put("childIsUsCitizen", "Yes");
            child2.put("ccapStartDate", "01/10/2025");

            Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", List.of(provider))
                    .with("children", List.of(child1, child2))
                    .withMultipleChildcareSchedulesForProvider(List.of(child1.get("uuid").toString(), child2.get("uuid").toString()), provider.get("uuid").toString())
                    .withSubmittedAtDate(OffsetDateTime.now())
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
                    .with("providerCareStartDate", "01/10/2025")
                    .with("providerResponseAgreeToCare", "true")
                    .with("currentProviderUuid", provider.get("uuid"))
                    .build());

            provider.put("providerResponseSubmissionId", providerSubmission.getId());

            sendEmailClass = new SendNewProviderAgreesToCareFamilyConfirmationEmail(sendEmailJob, messageSource,
                    submissionRepositoryService);
        }

        @AfterEach
        void tearDown() {
            submissionRepository.deleteAll();
        }

        @Test
        void correctlySetsEmailTemplateData() {
            Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission, provider);
            assertThat(emailDataOptional.isPresent()).isTrue();
            Map<String, Object> emailData = emailDataOptional.get();
            ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

            assertThat(emailTemplate.getSenderEmail()).isEqualTo(
                    new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
            assertThat(emailTemplate.getSubject()).isEqualTo(
                    messageSource.getMessage("email.response-email-for-family.provider-agrees.subject", null, locale));

            String emailCopy = emailTemplate.getBody().getValue();

            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-agrees.p1",
                            new Object[]{emailData.get("parentFirstName")}, locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.provider-agrees.p2-program",
                            new Object[]{emailData.get("childCareProgramName")},
                            locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p3",
                    new Object[]{"F.C. and S.C.", "January 10, 2025"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p4",
                    new Object[]{"ABC123"}, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.response-email-for-family.provider-agrees.p5",
                    new Object[]{"Sample Test CCRR", "(603) 555-1244"},
                    locale));
            assertThat(emailCopy).contains(
                    messageSource.getMessage("email.response-email-for-family.new-provider-agrees.note", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
            assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
        }
    }
}