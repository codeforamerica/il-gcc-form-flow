package org.ilgcc.jobs;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.ilgcc.app.utils.enums.TransmissionStatus.Queued;
import static org.ilgcc.app.utils.enums.TransmissionType.APPLICATION_PDF;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import static org.mockito.Mockito.*;

import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.logging.Logger;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.Transmission;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "WAIT_FOR_PROVIDER_RESPONSE_FLAG=true"
})
public class TransmissionsRecurringJobTest {
    @Autowired
    private TransmissionRepositoryService transmissionRepositoryService;
    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;
    @Autowired
    private TransmissionsRecurringJob transmissionsRecurringJob;
//    @MockBean
//    private EnqueueDocumentTransfer enqueueDocumentTransfer;

    @MockBean
    private PdfService mockedPdfService;
    @MockBean
    private CloudFileRepository mockedCloudFileRepository;
    @MockBean
    private PdfTransmissionJob mockedPdfTransmissionJob;
    @MockBean
    private Submission mockedSubmission;


    private Logger mockLogger;

    private Submission expiredSubmission;
    private Submission transmittedSubmission;
    private Submission unsubmittedSubmission;
    private Submission unexpiredSubmission;
    private Submission expiredUntransmittedSubmissionWithProviderResponse;

    @BeforeEach
    void setUp() {
        transmittedSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .with("providerResponseSubmissionId", "123124")
                .build();
        submissionRepositoryService.save(transmittedSubmission);
        transmissionRepositoryService.save(new Transmission(transmittedSubmission, null, Date.from(OffsetDateTime.now()
                .toInstant()), Queued, APPLICATION_PDF, null));

        unexpiredSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now())
                .build();
        submissionRepositoryService.save(unexpiredSubmission);

        expiredSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .build();
        submissionRepositoryService.save(expiredSubmission);

        unsubmittedSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .build();
        submissionRepositoryService.save(unsubmittedSubmission);

        expiredUntransmittedSubmissionWithProviderResponse = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .with("providerResponseSubmissionId", "123124")
                .build();
        submissionRepositoryService.save(expiredUntransmittedSubmissionWithProviderResponse);


//        verify(enqueueDocumentTransfer).enqueuePDFDocumentBySubmission(mockedPdfService, mockedCloudFileRepository, mockedPdfTransmissionJob, eq(mockedSubmission), anyString());
//        doNothing().when(enqueueDocumentTransfer).enqueueUploadedDocumentBySubmission(any(), any(), any(), any());

    }


    @Test
    void noProviderResponseJobWillLogIfAnySubmissionsAreMissingTransmissions() {
        MockedStatic<EnqueueDocumentTransfer> mockedEnqueueDocumentTransfer = mockStatic(EnqueueDocumentTransfer.class);
        transmissionsRecurringJob.noProviderResponseJob();
        mockedEnqueueDocumentTransfer.verify()
        verify(mockLogger).info(eq(String.format("Running the 'No provider response job' for %s submissions", 1)));
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
