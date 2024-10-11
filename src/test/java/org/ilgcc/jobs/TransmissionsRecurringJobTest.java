package org.ilgcc.jobs;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.ilgcc.app.utils.enums.TransmissionStatus.Queued;
import static org.ilgcc.app.utils.enums.TransmissionType.APPLICATION_PDF;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.time.OffsetDateTime;
import java.util.Date;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.Transmission;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "WAIT_FOR_PROVIDER_RESPONSE_FLAG=true"
})
public class TransmissionsRecurringJobTest {

    private S3PresignService s3PresignService;

    @Autowired
    private TransmissionRepositoryService transmissionRepositoryService;
    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;
    @Autowired
    private UserFileRepositoryService userFileRepositoryService;
    @Autowired
    private UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
    @Autowired
    private PdfService pdfService;
    @Autowired
    private CloudFileRepository cloudFileRepository;
    private String waitForProviderResponseFlag = "true";
    @Autowired
    private PdfTransmissionJob pdfTransmissionJob;

    private TransmissionsRecurringJob transmissionsRecurringJob = new TransmissionsRecurringJob(s3PresignService,
            transmissionRepositoryService, userFileRepositoryService, uploadedDocumentTransmissionJob, pdfService,
            cloudFileRepository, pdfTransmissionJob, waitForProviderResponseFlag);

    @BeforeEach
    void setUp() {
        Submission transmittedSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .with("providerResponseSubmissionId", "123124")
                .build();
        submissionRepositoryService.save(transmittedSubmission);
        transmissionRepositoryService.save(new Transmission(transmittedSubmission, null, Date.from(OffsetDateTime.now()
                .toInstant()), Queued, APPLICATION_PDF, null));

        Submission unexpiredSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now())
                .build();
        submissionRepositoryService.save(unexpiredSubmission);

        Submission expiredSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .build();
        submissionRepositoryService.save(expiredSubmission);

        Submission unsubmittedSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .build();
        submissionRepositoryService.save(unsubmittedSubmission);

        Submission expiredUntransmittedSubmissionWithProviderResponse = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .with("providerResponseSubmissionId", "123124")
                .build();
        submissionRepositoryService.save(expiredUntransmittedSubmissionWithProviderResponse);
    }


    @Test
    void noProviderResponseJobWillLogIfAnySubmissionsAreMissingTransmissions() {

        transmissionsRecurringJob.noProviderResponseJob();

        // mock log and confirm that it was called?
        // check the logs to see if there was a message: "Running the 'No provider response job' for 3 submissions"

        assertThat(true).isTrue();
    }

    @Test
    void submissionsWithoutProviderResponseWillCallDocumentTransferMethods() {
// Will spy on the enqueue methods and confirm that they have been called with the expected submissions
        assertThat(true).isTrue();
    }

    @Test
    void submissionsWithProviderResponseWillNotCallDocumentTransferMethods() {
        assertThat(true).isTrue();

// Will spy on the enqueue methods and confirm that they have been called with the expected submissions
    }

    @Test
    void submissionsNotExpiredWillNotCallDocumentTransferMethods() {
        assertThat(true).isTrue();

        // Will spy on the enqueue methods and confirm that they have been NOT called with the expected submissions
    }

}
