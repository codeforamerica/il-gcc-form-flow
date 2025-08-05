package org.ilgcc.app.submission.actions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import org.ilgcc.app.data.SubmissionSenderService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.jobs.CCMSSubmissionPayloadTransactionJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


@ActiveProfiles("test")
@SpringBootTest
class UploadProviderSubmissionToS3AndSendToCCMSTest {

    private UploadProviderSubmissionToS3AndSendToCCMS uploadProviderSubmissionToS3AndSendToCCMS;

    @MockitoBean
    private CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob;

    private Submission providerSubmission;

    private SubmissionSenderService submissionSenderService;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    @BeforeEach
    void setUp() {
        Submission familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now())
                .withShortCode("shortCodeTest")
                .build();

        submissionRepositoryService.save(familySubmission);

        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .with("familySubmissionId", familySubmission.getId().toString())
                .with("providerPaidCcap", true)
                .build();

        submissionRepositoryService.save(providerSubmission);
    }

    @Test
    void ProviderSubmissionIsEnqueued() {
        submissionSenderService = new SubmissionSenderService(
                submissionRepositoryService,
                ccmsSubmissionPayloadTransactionJob,
                true,
                false);

        uploadProviderSubmissionToS3AndSendToCCMS = new UploadProviderSubmissionToS3AndSendToCCMS(submissionSenderService);
        uploadProviderSubmissionToS3AndSendToCCMS.run(providerSubmission);
    }
}
