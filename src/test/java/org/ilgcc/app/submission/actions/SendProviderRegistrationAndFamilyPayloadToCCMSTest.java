package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@ActiveProfiles("test")
@SpringBootTest
class SendProviderRegistrationAndFamilyPayloadToCCMSTest {
  @Autowired
  SubmissionRepositoryService submissionRepositoryService;

  SendProviderRegistrationAndFamilyPayloadToCCMS sendProviderRegistrationAndFamilyPayloadToCCMS;

  @MockitoSpyBean
  CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob;

  Map<String, Object> provider1 = new HashMap<>();
  Map<String, Object> newProvider = new HashMap<>();

  private Submission familySubmission;
  private Submission providerSubmission;

  @BeforeEach
  void setUp() {
    provider1.put("uuid", "dayCareProvider-1");
    provider1.put("iterationIsComplete", true);
    provider1.put("providerFirstName", "FirstName");
    provider1.put("providerLastName", "LastName");

    newProvider.put("uuid", "newProviderDayCare-2");
    newProvider.put("iterationIsComplete", true);
    newProvider.put("providerFirstName", "New");
    newProvider.put("providerLastName", "Provider");
  }
  @Test
  public void whenAllProvidersHaveNotRespondedCCMSIsNotCalled(){
    familySubmission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .withParentDetails()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .withSubmittedAtDate(OffsetDateTime.now())
        .with("providers", List.of(provider1, newProvider))
        .with("providerApplicationResponseStatus", SubmissionStatus.ACTIVE)
        .withMultipleChildcareSchedulesForProvider(List.of("first-child"), provider1.get("uuid").toString())
        .withMultipleChildcareSchedulesForProvider(List.of("second-child"), newProvider.get("uuid").toString())
        .withShortCode("shortCodeTest")
        .build();

    submissionRepositoryService.save(familySubmission);

    providerSubmission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .with("providerPaidCcap", false)
        .with("currentProviderUuid", "newProviderDayCare-2")
        .build();
    submissionRepositoryService.save(providerSubmission);

    sendProviderRegistrationAndFamilyPayloadToCCMS = new SendProviderRegistrationAndFamilyPayloadToCCMS(
        submissionRepositoryService,
        ccmsSubmissionPayloadTransactionJob,
        true);

    sendProviderRegistrationAndFamilyPayloadToCCMS.run(providerSubmission);
    Submission newSubmission = submissionRepositoryService.findById(familySubmission.getId()).get();
    Map<String, Object> newProvider = SubmissionUtilities.getCurrentProvider(newSubmission.getInputData(), providerSubmission.getInputData().get("currentProviderUuid").toString());
    assertThat(newProvider.get("providerApplicationResponseStatus").toString()).isEqualTo(SubmissionStatus.RESPONDED.name());
    verifyNoInteractions(ccmsSubmissionPayloadTransactionJob);
  }

  @Test
  public void whenNewProviderIsActiveAndAllProvidersHaveRespondedCCMSIsCalled(){
    provider1.put("providerResponseSubmissionId", "providerSubmisson-1");
    provider1.put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());
    provider1.put("providerResponseAgreeToCare", "true");

    familySubmission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .withParentDetails()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .withSubmittedAtDate(OffsetDateTime.now())
        .with("providers", List.of(provider1, newProvider))
        .with("providerApplicationResponseStatus", SubmissionStatus.ACTIVE)
        .withMultipleChildcareSchedulesForProvider(List.of("first-child"), provider1.get("uuid").toString())
        .withMultipleChildcareSchedulesForProvider(List.of("second-child"), newProvider.get("uuid").toString())
        .withShortCode("shortCodeTest")
        .build();

    submissionRepositoryService.save(familySubmission);

    providerSubmission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .with("providerPaidCcap", false)
        .with("currentProviderUuid", "newProviderDayCare-2")
        .build();
    submissionRepositoryService.save(providerSubmission);

    sendProviderRegistrationAndFamilyPayloadToCCMS = new SendProviderRegistrationAndFamilyPayloadToCCMS(
        submissionRepositoryService,
        ccmsSubmissionPayloadTransactionJob,
        true);

    sendProviderRegistrationAndFamilyPayloadToCCMS.run(providerSubmission);
    Submission newSubmission = submissionRepositoryService.findById(familySubmission.getId()).orElse(new Submission());
    Map<String, Object> newProvider = SubmissionUtilities.getCurrentProvider(newSubmission.getInputData(), providerSubmission.getInputData().get("currentProviderUuid").toString());
    assertThat(newProvider.get("providerApplicationResponseStatus").toString()).isEqualTo(SubmissionStatus.RESPONDED.name());
    verify(ccmsSubmissionPayloadTransactionJob).enqueueCCMSTransactionPayloadWithDelay(familySubmission.getId());
  }

  @Test
  public void skipCCMSCallWhenProviderSubmissionHasExpired(){
    provider1.put("providerResponseSubmissionId", "providerSubmisson-1");
    provider1.put("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name());
    provider1.put("providerResponseAgreeToCare", "true");

    newProvider.put("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.name());

    familySubmission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .withParentDetails()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "true")
        .withSubmittedAtDate(OffsetDateTime.now())
        .with("providers", List.of(provider1, newProvider))
        .withMultipleChildcareSchedulesForProvider(List.of("first-child"), provider1.get("uuid").toString())
        .withMultipleChildcareSchedulesForProvider(List.of("second-child"), newProvider.get("uuid").toString())
        .with("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.name())
        .withShortCode("shortCodeTest")
        .build();
    submissionRepositoryService.save(familySubmission);

    providerSubmission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .with("providerPaidCcap", false)
        .with("currentProviderUuid", "newProviderDayCare-2")
        .build();

    submissionRepositoryService.save(providerSubmission);

    sendProviderRegistrationAndFamilyPayloadToCCMS = new SendProviderRegistrationAndFamilyPayloadToCCMS(
        submissionRepositoryService,
        ccmsSubmissionPayloadTransactionJob,
        true);

    sendProviderRegistrationAndFamilyPayloadToCCMS.run(providerSubmission);
    verifyNoInteractions(ccmsSubmissionPayloadTransactionJob);
  }
}