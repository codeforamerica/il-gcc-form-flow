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
class ProviderRespondedConfirmationEmailTest {
  @MockitoSpyBean
  SendEmailJob sendEmailJob;

  @Autowired
  SubmissionRepositoryService submissionRepositoryService;

  @Autowired
  SubmissionRepository submissionRepository;

  @Autowired
  MessageSource messageSource;

  private Submission providerSubmission;
  private Submission secondProviderSubmission;
  private ProviderRespondedConfirmationEmail sendEmailClass;

  private final Locale locale = Locale.ENGLISH;

  @Nested
  class WhenMultipleProvidersIsOffAndProviderIsAnExistingProviderRenderCorrectEmail {

    Map<String, Object> provider;
    @BeforeEach
    void setUp() {
      provider = new HashMap<>();
      provider.put("uuid", "first-provider-uuid");
      provider.put("iterationIsComplete", true);
      provider.put("familyIntendedProviderEmail", "familyChildCareEmail");

      Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
          .withFlow("gcc")
          .with("parentPreferredName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
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
          .with("providerPaidCcap", "true")
          .with("providerCareStartDate", "01/10/2025")
          .with("providerResponseAgreeToCare", "true")
          .build());

      provider.put("providerResponseSubmissionId", providerSubmission.getId());

      sendEmailClass = new ProviderRespondedConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @AfterEach
    void tearDown() {
      submissionRepository.deleteAll();
    }

    @Test
    void correctlySetsEmailRecipient() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
      Map<String, Object> emailData = emailDataOptional.orElseGet(HashMap::new);

      assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("provideremail@test.com");
    }

    @Test
    void correctlySetsEmailTemplate() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
      assertThat(emailDataOptional.isPresent()).isTrue();
      Map<String, Object> emailData = emailDataOptional.get();
      ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

      assertThat(emailTemplate.getSenderEmail()).isEqualTo(
          new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
      assertThat(emailTemplate.getSubject()).isEqualTo(
          messageSource.getMessage("email.general.subject.confirmation-code", new Object[]{emailData.get("confirmationCode")}, locale));

      String emailCopy = emailTemplate.getBody().getValue();

      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p1", null, locale));
      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p2", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p3",
          new Object[]{"F.C. and S.C.", "January 10, 2025"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p4",
          new Object[]{"ABC123"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p5",
          new Object[]{emailData.get("ccrrName")},
          locale));
      assertThat(emailCopy).doesNotContain(messageSource.getMessage("email.provider-confirmation-after-response.p6", null, locale));
      assertThat(emailCopy).doesNotContain(messageSource.getMessage("email.provider-confirmation-after-response.p7", new Object[]{emailData.get("ccrrName")}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }
  }

  @Nested
  class WhenMultipleProvidersIsOffAndProviderIsNewRenderCorrectEmail {

    Map<String, Object> provider;
    @BeforeEach
    void setUp() {
      provider = new HashMap<>();
      provider.put("uuid", "first-provider-uuid");
      provider.put("iterationIsComplete", true);
      provider.put("familyIntendedProviderEmail", "familyChildCareEmail");

      Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
          .withFlow("gcc")
          .with("parentPreferredName", "FirstName").withChild("First", "Child", "true").withChild("Second", "Child", "true")
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
          .with("providerPaidCcap", "false")
          .with("providerResponseAgreeToCare", "true")
          .build());

      provider.put("providerResponseSubmissionId", providerSubmission.getId());

      sendEmailClass = new ProviderRespondedConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @AfterEach
    void tearDown() {
      submissionRepository.deleteAll();
    }

    @Test
    void correctlySetsEmailRecipient() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
      Map<String, Object> emailData = emailDataOptional.orElseGet(HashMap::new);

      assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("provideremail@test.com");
    }

    @Test
    void correctlySetsEmailTemplate() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
      assertThat(emailDataOptional.isPresent()).isTrue();
      Map<String, Object> emailData = emailDataOptional.get();
      ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

      assertThat(emailTemplate.getSenderEmail()).isEqualTo(
          new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
      assertThat(emailTemplate.getSubject()).isEqualTo(
          messageSource.getMessage("email.general.subject.confirmation-code", new Object[]{emailData.get("confirmationCode")}, locale));

      String emailCopy = emailTemplate.getBody().getValue();

      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p1", null, locale));
      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p2", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p3",
          new Object[]{"F.C. and S.C.", "January 10, 2025"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p4",
          new Object[]{"ABC123"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p5",
          new Object[]{emailData.get("ccrrName")},
          locale));
      assertThat(emailCopy).doesNotContain(messageSource.getMessage("email.provider-confirmation-after-response.p6", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p7", new Object[]{emailData.get("ccrrName")}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }
  }

  @Nested
  class WhenMultipleProvidersIsOnWithSameProviderForEveryChildAndProviderIsNewRenderEmail {

    Map<String, Object> provider;
    Map<String, Object> secondProvider;

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
          .with("providers", List.of(provider, secondProvider))
          .with("children", List.of(child1, child2))
          .withMultipleChildcareSchedulesAllBelongingToTheSameProvider(List.of(child1.get("uuid").toString(), child2.get("uuid").toString()), provider.get("uuid").toString())
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
          .with("providerPaidCcap", "false")
          .with("providerResponseAgreeToCare", "true")
          .build());

      provider.put("providerResponseSubmissionId", providerSubmission.getId());

      sendEmailClass = new ProviderRespondedConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @AfterEach
    void tearDown() {
      submissionRepository.deleteAll();
    }

    @Test
    void correctlySetsEmailRecipient() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
      Map<String, Object> emailData = emailDataOptional.orElseGet(HashMap::new);

      assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("provideremail@test.com");
    }

    @Test
    void correctlySetsEmailTemplate() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
      assertThat(emailDataOptional.isPresent()).isTrue();
      Map<String, Object> emailData = emailDataOptional.get();
      ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

      assertThat(emailTemplate.getSenderEmail()).isEqualTo(
          new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
      assertThat(emailTemplate.getSubject()).isEqualTo(
          messageSource.getMessage("email.general.subject.confirmation-code", new Object[]{emailData.get("confirmationCode")}, locale));

      String emailCopy = emailTemplate.getBody().getValue();

      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p1", null, locale));
      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p2", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p3",
          new Object[]{"F.C. and S.C.", "January 10, 2025"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p4",
          new Object[]{"ABC123"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p5",
          new Object[]{emailData.get("ccrrName")},
          locale));
      assertThat(emailCopy).doesNotContain(messageSource.getMessage("email.provider-confirmation-after-response.p6", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p7", new Object[]{emailData.get("ccrrName")}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }
  }

  @Nested
  class WhenMultipleProvidersIsOnWithSameProviderAndIsAnExistingProviderRenderEmail {

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
          .withMultipleChildcareSchedulesAllBelongingToTheSameProvider(List.of(child1.get("uuid").toString()), provider.get("uuid").toString())
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
          .with("providerPaidCcap", "true")
          .with("providerResponseAgreeToCare", "true")
          .with("currentProviderUuid", provider.get("uuid").toString())
          .build());

      provider.put("providerResponseSubmissionId", providerSubmission.getId());
      sendEmailClass = new ProviderRespondedConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @AfterEach
    void tearDown() {
      submissionRepository.deleteAll();
    }

    @Test
    void correctlySetsEmailRecipient() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
      Map<String, Object> emailData = emailDataOptional.orElseGet(HashMap::new);

      assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("provideremail@test.com");
    }

    @Test
    void correctlySetsEmailTemplate() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
      assertThat(emailDataOptional.isPresent()).isTrue();
      Map<String, Object> emailData = emailDataOptional.get();
      ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

      assertThat(emailTemplate.getSenderEmail()).isEqualTo(
          new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
      assertThat(emailTemplate.getSubject()).isEqualTo(
          messageSource.getMessage("email.general.subject.confirmation-code", new Object[]{emailData.get("confirmationCode")}, locale));

      String emailCopy = emailTemplate.getBody().getValue();

      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p1", null, locale));
      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p2", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p3",
          new Object[]{"F.C.", "January 10, 2025"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p4",
          new Object[]{"ABC123"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p5",
          new Object[]{emailData.get("ccrrName")},
          locale));
      assertThat(emailCopy).doesNotContain(messageSource.getMessage("email.provider-confirmation-after-response.p6", null, locale));
      assertThat(emailCopy).doesNotContain(messageSource.getMessage("email.provider-confirmation-after-response.p7", new Object[]{emailData.get("ccrrName")}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }
  }

  @Nested
  class WhenMultipleProvidersIsOnAndMoreThanOneProviderHasAChildcareScheduleAndIsAnExistingProviderRenderCorrectEmail {

    Map<String, Object> provider;
    Map<String, Object> secondProvider;
    @BeforeEach
    void setUp() {
      provider = new HashMap<>();
      provider.put("uuid", "first-provider-uuid");
      provider.put("iterationIsComplete", true);
      provider.put("familyIntendedProviderEmail", "familyChildCareEmail");

      secondProvider = new HashMap<>();
      secondProvider.put("uuid", "second-provider-uuid");
      secondProvider.put("iterationIsComplete", true);
      secondProvider.put("familyIntendedProviderEmail", "secondFamilyChildCareEmail");

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

      Map<String, Object> child3 = new HashMap<>();
      child3.put("uuid", "child-3-uuid");
      child3.put("childFirstName", "Third");
      child3.put("childLastName", "Person");
      child3.put("childInCare", "true");
      child3.put("childDateOfBirthMonth", "10");
      child3.put("childDateOfBirthDay", "11");
      child3.put("childDateOfBirthYear", "2002");
      child3.put("needFinancialAssistanceForChild", "true");
      child3.put("childIsUsCitizen", "Yes");
      child3.put("ccapStartDate", "01/10/2025");

      Map<String, Object> child4 = new HashMap<>();
      child4.put("uuid", "child-4-uuid");
      child4.put("childFirstName", "Fourth");
      child4.put("childLastName", "Kid");
      child4.put("childInCare", "true");
      child4.put("childDateOfBirthMonth", "10");
      child4.put("childDateOfBirthDay", "11");
      child4.put("childDateOfBirthYear", "2002");
      child4.put("needFinancialAssistanceForChild", "true");
      child4.put("childIsUsCitizen", "Yes");
      child4.put("ccapStartDate", "01/10/2025");


      Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
          .withFlow("gcc")
          .with("parentFirstName", "FirstName")
          .with("parentContactEmail", "familyemail@test.com")
          .with("languageRead", "English")
          .with("providers", List.of(provider, secondProvider))
          .with("children", List.of(child1, child2, child3, child4))
          .withMultipleChildcareSchedulesForProvider(List.of(child2.get("uuid").toString(), child4.get("uuid").toString()), secondProvider.get("uuid").toString())
          .withMultipleChildcareSchedulesForProvider(List.of(child1.get("uuid").toString(), child3.get("uuid").toString()), provider.get("uuid").toString())
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
          .with("providerPaidCcap", "true")
          .with("providerResponseAgreeToCare", "true")
          .build());

      secondProviderSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
          .withFlow("providerresponse")
          .with("familySubmissionId", familySubmission.getId().toString())
          .with("providerResponseContactEmail", "secondProvideremail@test.com")
          .with("providerResponseFirstName", "SecondProviderFirst")
          .with("providerResponseLastName", "LastNameSecondProvider")
          .with("providerResponseBusinessName", "SecondBusinessName")
          .with("providerCareStartDate", "01/01/2025")
          .with("providerPaidCcap", "true")
          .with("providerResponseAgreeToCare", "true")
          .with("currentProviderUuid", secondProvider.get("uuid").toString())
          .build());

      secondProvider.put("providerResponseSubmissionId", secondProviderSubmission.getId());

      sendEmailClass = new ProviderRespondedConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @AfterEach
    void tearDown() {
      submissionRepository.deleteAll();
    }

    @Test
    void correctlySetsEmailRecipient() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(secondProviderSubmission);

      Map<String, Object> emailData = emailDataOptional.orElseGet(HashMap::new);

      assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("secondProvideremail@test.com");
    }

    @Test
    void correctlySetsEmailTemplate() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(secondProviderSubmission);
      assertThat(emailDataOptional.isPresent()).isTrue();
      Map<String, Object> emailData = emailDataOptional.get();
      ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

      assertThat(emailTemplate.getSenderEmail()).isEqualTo(
          new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
      assertThat(emailTemplate.getSubject()).isEqualTo(
          messageSource.getMessage("email.general.subject.confirmation-code", new Object[]{emailData.get("confirmationCode")}, locale));

      String emailCopy = emailTemplate.getBody().getValue();

      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p1", null, locale));
      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p2", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p3",
          new Object[]{"S.C. and F.K.", "January 01, 2025"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p4",
          new Object[]{"ABC123"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p5",
          new Object[]{emailData.get("ccrrName")},
          locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p6", null, locale));
      assertThat(emailCopy).doesNotContain(messageSource.getMessage("email.provider-confirmation-after-response.p7", new Object[]{emailData.get("ccrrName")}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }
  }

  @Nested
  class WhenMultipleProvidersIsOnAndMoreThanOneProviderHasAChildcareScheduleAndIsANewProviderRenderCorrectEmail {

    Map<String, Object> provider;
    Map<String, Object> secondProvider;

    @BeforeEach
    void setUp() {
      provider = new HashMap<>();
      provider.put("uuid", "first-provider-uuid");
      provider.put("iterationIsComplete", true);
      provider.put("familyIntendedProviderEmail", "familyChildCareEmail");

      secondProvider = new HashMap<>();
      secondProvider.put("uuid", "second-provider-uuid");
      secondProvider.put("iterationIsComplete", true);
      secondProvider.put("familyIntendedProviderEmail", "secondFamilyChildCareEmail");

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

      Map<String, Object> child3 = new HashMap<>();
      child3.put("uuid", "child-3-uuid");
      child3.put("childFirstName", "Third");
      child3.put("childLastName", "Person");
      child3.put("childInCare", "true");
      child3.put("childDateOfBirthMonth", "10");
      child3.put("childDateOfBirthDay", "11");
      child3.put("childDateOfBirthYear", "2002");
      child3.put("needFinancialAssistanceForChild", "true");
      child3.put("childIsUsCitizen", "Yes");
      child3.put("ccapStartDate", "01/10/2025");

      Map<String, Object> child4 = new HashMap<>();
      child4.put("uuid", "child-4-uuid");
      child4.put("childFirstName", "Fourth");
      child4.put("childLastName", "Kid");
      child4.put("childInCare", "true");
      child4.put("childDateOfBirthMonth", "10");
      child4.put("childDateOfBirthDay", "11");
      child4.put("childDateOfBirthYear", "2002");
      child4.put("needFinancialAssistanceForChild", "true");
      child4.put("childIsUsCitizen", "Yes");
      child4.put("ccapStartDate", "01/10/2025");

      Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
          .withFlow("gcc")
          .with("parentFirstName", "FirstName")
          .with("parentContactEmail", "familyemail@test.com")
          .with("languageRead", "English")
          .with("providers", List.of(provider, secondProvider))
          .with("children", List.of(child1, child2, child3, child4))
          .withMultipleChildcareSchedulesForProvider(List.of(child1.get("uuid").toString(), child2.get("uuid").toString(), child3.get("uuid").toString()), provider.get("uuid").toString())
          .withMultipleChildcareSchedulesForProvider(List.of(child4.get("uuid").toString()), secondProvider.get("uuid").toString())
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
          .with("providerPaidCcap", "false")
          .with("currentProviderUuid", provider.get("uuid").toString())
          .with("providerResponseAgreeToCare", "true")
          .build());

      secondProviderSubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
          .withFlow("providerresponse")
          .with("familySubmissionId", familySubmission.getId().toString())
          .with("providerResponseContactEmail", "secondProvideremail@test.com")
          .with("providerResponseFirstName", "SecondProviderFirst")
          .with("providerResponseLastName", "LastNameSecondProvider")
          .with("providerResponseBusinessName", "SecondBusinessName")
          .with("providerCareStartDate", "01/01/2025")
          .with("providerPaidCcap", "true")
          .with("providerResponseAgreeToCare", "true")
          .build());

      provider.put("providerResponseSubmissionId", providerSubmission.getId());

      sendEmailClass = new ProviderRespondedConfirmationEmail(sendEmailJob, messageSource, submissionRepositoryService);
    }

    @AfterEach
    void tearDown() {
      submissionRepository.deleteAll();
    }

    @Test
    void correctlySetsEmailRecipient() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
      Map<String, Object> emailData = emailDataOptional.orElseGet(HashMap::new);

      assertThat(sendEmailClass.getRecipientEmail(emailData)).isEqualTo("provideremail@test.com");
    }

    @Test
    void correctlySetsEmailTemplate() {
      Optional<Map<String, Object>> emailDataOptional = sendEmailClass.getEmailData(providerSubmission);
      assertThat(emailDataOptional.isPresent()).isTrue();
      Map<String, Object> emailData = emailDataOptional.get();
      ILGCCEmailTemplate emailTemplate = sendEmailClass.emailTemplate(emailData);

      assertThat(emailTemplate.getSenderEmail()).isEqualTo(
          new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale)));
      assertThat(emailTemplate.getSubject()).isEqualTo(
          messageSource.getMessage("email.general.subject.confirmation-code", new Object[]{emailData.get("confirmationCode")}, locale));

      String emailCopy = emailTemplate.getBody().getValue();

      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p1", null, locale));
      assertThat(emailCopy).contains(
          messageSource.getMessage("email.provider-confirmation-after-response.p2", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p3",
          new Object[]{"F.C., S.C. and T.P.", "January 10, 2025"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p4",
          new Object[]{"ABC123"}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p5",
          new Object[]{emailData.get("ccrrName")},
          locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p6", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.provider-confirmation-after-response.p7", new Object[]{emailData.get("ccrrName")}, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.automated-response", null, locale));
      assertThat(emailCopy).contains(messageSource.getMessage("email.general.footer.cfa", null, locale));
    }
  }
}