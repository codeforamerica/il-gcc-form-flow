package org.ilgcc.jobs;

import static org.ilgcc.app.utils.enums.TransmissionStatus.Queued;
import static org.ilgcc.app.utils.enums.TransmissionType.APPLICATION_PDF;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.JobrunrJobRepository;
import org.ilgcc.app.data.TransactionRepository;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.data.Transmission;
import org.ilgcc.app.data.TransmissionRepository;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.SendProviderDidNotRespondToFamilyEmail;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@Deprecated
@SpringBootTest(
        classes = IlGCCApplication.class
)
@ActiveProfiles("test")
public class ProviderResponseRecurringJobTest {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private TransmissionRepository transmissionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransmissionRepositoryService transmissionRepositoryService;

    @Autowired
    private TransactionRepositoryService transactionRepositoryService;

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
    private JobrunrJobRepository jobrunrJobRepository;

    @Mock
    private PdfTransmissionJob pdfTransmissionJob;

    @Autowired
    private ApplicationContext context;

    @Mock
    private EnqueueDocumentTransfer enqueueDocumentTransfer;

    private ProviderResponseRecurringJob providerResponseRecurringJob;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;
    
    @MockitoBean
    private CCMSSubmissionPayloadTransactionJob ccmsSubmissionPayloadTransactionJob;

    private Submission expiredSubmission;
    private Submission transmittedSubmission;
    private Submission unsubmittedSubmission;
    private Submission unexpiredSubmission;
    private Submission expiredUntransmittedSubmissionWithProviderResponse;
    private Submission providerSubmission;

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    MessageSource messageSource;

    private SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;

    @BeforeEach
    void setUp() {
        sendProviderDidNotRespondToFamilyEmail = new SendProviderDidNotRespondToFamilyEmail(sendEmailJob, messageSource, submissionRepositoryService);
        providerResponseRecurringJob = new ProviderResponseRecurringJob(
                s3PresignService,
                transmissionRepositoryService,
                transactionRepositoryService,
                userFileRepositoryService,
                uploadedDocumentTransmissionJob,
                pdfService,
                cloudFileRepository,
                jobrunrJobRepository,
                pdfTransmissionJob,
                enqueueDocumentTransfer,
                submissionRepositoryService,
                ccmsSubmissionPayloadTransactionJob,
                true,
                true,
                sendProviderDidNotRespondToFamilyEmail
        );
    }

    @AfterEach
    protected void clearSubmissions() {
        transmissionRepository.deleteAll();
        transactionRepository.deleteAll();
        submissionRepository.deleteAll();
    }

    @Test
    public void providerResponseRecurringJobEnabledWhenFlagIsOn() {
        assertTrue(context.containsBean("providerResponseRecurringJob"));
    }

//    @Test
    void enqueueDocumentTransferIsOnlyCalledOnExpiredSubmissions() {
        unexpiredSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .with("parentContactEmail", "test-unexpired@mail.com")
                .withSubmittedAtDate(OffsetDateTime.now())
                .withFlow("gcc")
                .build();
        submissionRepository.save(unexpiredSubmission);

        OffsetDateTime expiredSubmissionDate = ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(OffsetDateTime.now()).minusMinutes(5);
        expiredSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .with("parentContactEmail", "test-expired@mail.com")
                .withSubmittedAtDate(expiredSubmissionDate)
                .withFlow("gcc")
                .build();
        submissionRepository.save(expiredSubmission);

        unsubmittedSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .with("parentContactEmail", "test-unsubmitted@mail.com")
                .withFlow("gcc")
                .build();
        submissionRepository.save(unsubmittedSubmission);

        providerResponseRecurringJob.runNoProviderResponseJob();

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
        verify(sendEmailJob).enqueueSendEmailJob(any(ILGCCEmail.class));
    }

    @Test
    void enqueueDocumentTransferWillNotBeCalledIfSubmissionHasTransmissionAndTransaction() {
        transmittedSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .with("providerResponseSubmissionId", "4e477c74-8529-4cd0-a02b-2d67e5b5b171")
                .withFlow("gcc")
                .build();
        transmittedSubmission = submissionRepository.save(transmittedSubmission);
        transmissionRepositoryService.save(new Transmission(transmittedSubmission, null, Date.from(OffsetDateTime.now()
                .toInstant()), Queued, APPLICATION_PDF, null));

        transactionRepositoryService.createTransaction(UUID.randomUUID(), transmittedSubmission.getId(), null);

        providerResponseRecurringJob.runNoProviderResponseJob();

        verifyNoInteractions(enqueueDocumentTransfer, pdfService, userFileRepositoryService, sendEmailJob);
    }

    @Test
    void enqueueDocumentTransferWillNotBeCalledIfSubmissionHasTransmissionOnly_CCMS_Off() {
        providerResponseRecurringJob = new ProviderResponseRecurringJob(
                s3PresignService,
                transmissionRepositoryService,
                transactionRepositoryService,
                userFileRepositoryService,
                uploadedDocumentTransmissionJob,
                pdfService,
                cloudFileRepository,
                jobrunrJobRepository,
                pdfTransmissionJob,
                enqueueDocumentTransfer,
                submissionRepositoryService,
                ccmsSubmissionPayloadTransactionJob,
                false,
                true,
                sendProviderDidNotRespondToFamilyEmail
        );

        transmittedSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .with("providerResponseSubmissionId", "4e477c74-8529-4cd0-a02b-2d67e5b5b171")
                .withFlow("gcc")
                .build();
        transmittedSubmission = submissionRepository.save(transmittedSubmission);

        transmissionRepositoryService.save(new Transmission(transmittedSubmission, null, Date.from(OffsetDateTime.now()
                .toInstant()), Queued, APPLICATION_PDF, null));

        providerResponseRecurringJob.runNoProviderResponseJob();

        verifyNoInteractions(enqueueDocumentTransfer, pdfService, userFileRepositoryService, sendEmailJob);
    }

    @Test
    void enqueueDocumentTransferWillNotBeCalledIfSubmissionHasTransactionOnly_DTS_Off() {
        providerResponseRecurringJob = new ProviderResponseRecurringJob(
                s3PresignService,
                transmissionRepositoryService,
                transactionRepositoryService,
                userFileRepositoryService,
                uploadedDocumentTransmissionJob,
                pdfService,
                cloudFileRepository,
                jobrunrJobRepository,
                pdfTransmissionJob,
                enqueueDocumentTransfer,
                submissionRepositoryService,
                ccmsSubmissionPayloadTransactionJob,
                true,
                false,
                sendProviderDidNotRespondToFamilyEmail
        );

        transmittedSubmission = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .with("providerResponseSubmissionId", "4e477c74-8529-4cd0-a02b-2d67e5b5b171")
                .withFlow("gcc")
                .build();
        transmittedSubmission = submissionRepository.save(transmittedSubmission);

        transactionRepositoryService.createTransaction(UUID.randomUUID(), transmittedSubmission.getId(), null);

        providerResponseRecurringJob.runNoProviderResponseJob();

        verifyNoInteractions(enqueueDocumentTransfer, pdfService, userFileRepositoryService, sendEmailJob);
    }

    @Test
    void enqueueDocumentTransferIsNotCalledOnExpiredUntransmittedSubmission() {

        providerSubmission = new SubmissionTestBuilder().withProviderSubmissionData().withSubmittedAtDate(OffsetDateTime.now().minusDays(2)).build();
        submissionRepository.save(providerSubmission);

        expiredUntransmittedSubmissionWithProviderResponse = new SubmissionTestBuilder()
                .withParentDetails()
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId().toString())
                .build();
        submissionRepository.save(expiredUntransmittedSubmissionWithProviderResponse);

        providerResponseRecurringJob.runNoProviderResponseJob();

        verify(enqueueDocumentTransfer, never()).enqueuePDFDocumentBySubmission(any(), any(), any(), any(), any());
        verify(enqueueDocumentTransfer, never()).enqueueUploadedDocumentBySubmission(any(), any(), any(), any());
        verifyNoInteractions(sendEmailJob);
    }
}
