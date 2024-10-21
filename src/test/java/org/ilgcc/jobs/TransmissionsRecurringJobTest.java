package org.ilgcc.jobs;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.util.Date;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.Transmission;
import org.ilgcc.app.data.TransmissionRepository;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.ilgcc.app.utils.enums.TransmissionStatus.Queued;
import static org.ilgcc.app.utils.enums.TransmissionType.APPLICATION_PDF;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
public class TransmissionsRecurringJobTest {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private TransmissionRepository transmissionRepository;

    @Autowired
    private TransmissionRepositoryService transmissionRepositoryService;
    @Mock
    private S3PresignService s3PresignService;

    @Mock
    private UserFileRepositoryService userFileRepositoryService;

    @Mock
    private UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;

    @Mock
    private PdfService pdfService;

    @Mock
    private CloudFileRepository cloudFileRepository;

    @Mock
    private PdfTransmissionJob pdfTransmissionJob;

    @Mock
    private EnqueueDocumentTransfer enqueueDocumentTransfer;

    @InjectMocks
    private TransmissionsRecurringJob transmissionsRecurringJob;

    private Submission expiredSubmission;
    private Submission transmittedSubmission;
    private Submission unsubmittedSubmission;
    private Submission unexpiredSubmission;
    private Submission expiredUntransmittedSubmissionWithProviderResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        transmissionsRecurringJob = new TransmissionsRecurringJob(
                s3PresignService,
                transmissionRepositoryService,
                userFileRepositoryService,
                uploadedDocumentTransmissionJob,
                pdfService,
                cloudFileRepository,
                pdfTransmissionJob,
                true,
                enqueueDocumentTransfer
        );
    }

    @AfterEach
    protected void clearSubmissions() {
        transmissionRepository.deleteAll();
        submissionRepository.deleteAll();
    }

    @Test
    void enqueueDocumentTransferWillNotRunIfFlagIsOff() {
        expiredSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .withFlow("gcc")
                .build();
        submissionRepository.save(expiredSubmission);

        transmissionsRecurringJob = new TransmissionsRecurringJob(
                s3PresignService,
                transmissionRepositoryService,
                userFileRepositoryService,
                uploadedDocumentTransmissionJob,
                pdfService,
                cloudFileRepository,
                pdfTransmissionJob,
                false,
                enqueueDocumentTransfer
        );

        transmissionsRecurringJob.noProviderResponseJob();

        verifyNoInteractions(enqueueDocumentTransfer);
    }
    @Test
    void enqueueDocumentTransferIsOnlyCalledOnExpiredSubmissions() {
        unexpiredSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now())
                .withFlow("gcc")
                .build();
        submissionRepository.save(unexpiredSubmission);

        expiredSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .withFlow("gcc")
                .build();
        submissionRepository.save(expiredSubmission);

        unsubmittedSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withFlow("gcc")
                .build();
        submissionRepository.save(unsubmittedSubmission);

        transmissionsRecurringJob.noProviderResponseJob();

        //Confirms that the method was called on the expired submission
        verify(enqueueDocumentTransfer, times(1)).enqueuePDFDocumentBySubmission(eq(pdfService), eq(cloudFileRepository),
                eq(pdfTransmissionJob), eq(expiredSubmission), any());
        verify(enqueueDocumentTransfer, times(1)).enqueueUploadedDocumentBySubmission(eq(userFileRepositoryService),
                eq(uploadedDocumentTransmissionJob), eq(s3PresignService), eq(expiredSubmission));

        verify(enqueueDocumentTransfer, never()).enqueuePDFDocumentBySubmission(eq(pdfService), eq(cloudFileRepository),
                eq(pdfTransmissionJob), eq(unexpiredSubmission), any());
        verify(enqueueDocumentTransfer, never()).enqueueUploadedDocumentBySubmission(eq(userFileRepositoryService),
                eq(uploadedDocumentTransmissionJob), eq(s3PresignService), eq(unexpiredSubmission));

        verify(enqueueDocumentTransfer, never()).enqueuePDFDocumentBySubmission(eq(pdfService), eq(cloudFileRepository),
                eq(pdfTransmissionJob), eq(unsubmittedSubmission), any());
        verify(enqueueDocumentTransfer, never()).enqueueUploadedDocumentBySubmission(eq(userFileRepositoryService),
                eq(uploadedDocumentTransmissionJob), eq(s3PresignService), eq(unsubmittedSubmission));
    }

    @Test
    void enqueueDocumentTransferWillNotBeCalledIfSubmissionHasTransmission() {
        transmittedSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .with("providerResponseSubmissionId", "123124")
                .withFlow("gcc")
                .build();
        submissionRepository.save(transmittedSubmission);
        transmissionRepositoryService.save(new Transmission(transmittedSubmission, null, Date.from(OffsetDateTime.now()
                .toInstant()), Queued, APPLICATION_PDF, null));

        transmissionsRecurringJob.noProviderResponseJob();

        verifyNoInteractions(enqueueDocumentTransfer, pdfService, userFileRepositoryService);
    }

    @Test
    void enqueueDocumentTransferIsNotCalledOnExpiredUntransmittedSubmission() {
        expiredUntransmittedSubmissionWithProviderResponse = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .withFlow("gcc")
                .with("providerResponseSubmissionId", "123124")
                .build();
        submissionRepository.save(expiredUntransmittedSubmissionWithProviderResponse);

        transmissionsRecurringJob.noProviderResponseJob();

        verify(enqueueDocumentTransfer, never()).enqueuePDFDocumentBySubmission(any(), any(), any(), any(), any());
        verify(enqueueDocumentTransfer, never()).enqueueUploadedDocumentBySubmission(any(), any(), any(), any());
    }
}
