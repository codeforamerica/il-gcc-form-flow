package org.ilgcc.app.submission.actions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.SubmissionSenderService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@ActiveProfiles("test")
@SpringBootTest
class UploadProviderSubmissionToS3AndSendToCCMSTest {

    private UploadProviderSubmissionToS3AndSendToCCMS uploadProviderSubmissionToS3AndSendToCCMS;
    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    @MockitoBean
    private CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob;

    private Submission familySubmission;

    private Submission providerSubmission;

    private SubmissionSenderService submissionSenderService;

    Map<String, Object> provider1 = new HashMap<>();
    Map<String, Object> provider2 = new HashMap<>();
    @BeforeEach
    void setUp() {


        provider1.put("uuid", "dayCareProvider-1");
        provider1.put("iterationIsComplete", true);
        provider1.put("providerFirstName", "FirstName");
        provider1.put("providerLastName", "LastName");

        provider2.put("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.toString());
        provider2.put("uuid", "dayCareProvider-2");
        provider2.put("iterationIsComplete", true);
        provider2.put("providerFirstName", "FirstName");
        provider2.put("providerLastName", "LastName");

        familySubmission = new SubmissionTestBuilder()
            .withFlow("gcc")
            .withParentDetails()
            .withChild("First", "Child", "true")
            .withChild("Second", "Child", "true")
            .withSubmittedAtDate(OffsetDateTime.now())
            .with("providers", List.of(provider1, provider2))
            .withMultipleChildcareSchedulesForProvider(List.of("first-child"), provider1.get("uuid").toString())
            .withMultipleChildcareSchedulesForProvider(List.of("second-child"), provider1.get("uuid").toString())
            .withShortCode("shortCodeTest")
            .build();

        submissionRepositoryService.save(familySubmission);
    }

    @Test
    void ProviderSubmissionIsEnqueuedWhenProviderSubmissionIsActive() {
        providerSubmission = new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .with("familySubmissionId", familySubmission.getId().toString())
            .with("providerPaidCcap", true)
            .with("currentProviderUuid", "dayCareProvider-1")
            .build();

        submissionRepositoryService.save(providerSubmission);

        submissionSenderService = new SubmissionSenderService(
                submissionRepositoryService,
                ccmsSubmissionPayloadTransactionJob,
                false);

        uploadProviderSubmissionToS3AndSendToCCMS = new UploadProviderSubmissionToS3AndSendToCCMS(submissionSenderService, submissionRepositoryService);
        uploadProviderSubmissionToS3AndSendToCCMS.run(providerSubmission);
        verify(ccmsSubmissionPayloadTransactionJob.enqueueCCMSTransactionPayloadWithDelay(any(UUID.class));
    }

    @Test
    void ProviderSubmissionIsEnqueuedWhenProviderSubmissionIsDisabled() {
        providerSubmission = new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .with("familySubmissionId", familySubmission.getId().toString())
            .with("providerPaidCcap", true)
            .with("currentProviderUuid", "dayCareProvider-2")
            .build();

        submissionRepositoryService.save(providerSubmission);

        submissionSenderService = new SubmissionSenderService(
            submissionRepositoryService,
            ccmsSubmissionPayloadTransactionJob,
            true,
            false);

        uploadProviderSubmissionToS3AndSendToCCMS = new UploadProviderSubmissionToS3AndSendToCCMS(submissionSenderService, submissionRepositoryService);
        uploadProviderSubmissionToS3AndSendToCCMS.run(providerSubmission);
    }
}
