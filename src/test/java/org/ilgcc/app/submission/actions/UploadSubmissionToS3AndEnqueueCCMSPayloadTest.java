package org.ilgcc.app.submission.actions;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.io.IOException;
import java.time.OffsetDateTime;
import org.ilgcc.app.data.SubmissionSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@ActiveProfiles("test")
@SpringBootTest
// TODO: Rename this class to just "SendSubmissionToCCMSTest" once mega PR is approved. -Marc
class UploadSubmissionToS3AndEnqueueCCMSPayloadTest {

    @Autowired
    private UploadSubmissionToS3AndEnqueueCCMSPayload uploadSubmissionToS3AndEnqueueCCMSPayload;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    @MockitoSpyBean
    private SubmissionSenderService submissionSenderService;

    private Submission submission;

    @BeforeEach
    void setUp() {
        submission = new Submission();
        submission = submissionRepositoryService.save(submission);
    }

    @Test
    void whenRun_thenNoProviderSubmissionIsSentToCCMS() throws IOException {

        submission.setSubmittedAt(OffsetDateTime.now());
        submission.getInputData().put("hasChosenProvider", "false");

        uploadSubmissionToS3AndEnqueueCCMSPayload.run(submission);

        verify(submissionSenderService).sendFamilySubmission(submission);
    }

    @Test
    void whenRun_thenProviderSubmissionIsNotSentToCCMS() throws IOException {

        submission.setSubmittedAt(OffsetDateTime.now());
        submission.getInputData().put("hasChosenProvider", "true");

        uploadSubmissionToS3AndEnqueueCCMSPayload.run(submission);

        verifyNoInteractions(submissionSenderService);
    }
}
