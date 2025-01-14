package org.ilgcc.app.submission.actions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.time.OffsetDateTime;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.PdfTransmissionJob;
import org.ilgcc.jobs.UploadedDocumentTransmissionJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest
class UploadProviderSubmissionToS3Test {

  @MockitoBean
  private PdfService pdfService;

  @MockitoBean
  private CloudFileRepository cloudFileRepository;
  @MockitoBean
  PdfTransmissionJob pdfTransmissionJob;
  @Mock
  private EnqueueDocumentTransfer enqueueDocumentTransfer;

  @InjectMocks
  private UploadProviderSubmissionToS3 uploadProviderSubmissionToS3;


  private Submission familySubmission;
  Submission providerSubmission;

  @Autowired
  private SubmissionRepositoryService submissionRepositoryService;
  @Autowired
  private UserFileRepositoryService userFileRepositoryService;
  @MockitoBean
  private UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
  @MockitoBean
  private S3PresignService s3PresignService;

  UploadProviderSubmissionToS3Test() {
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    familySubmission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .withParentDetails()
        .withSubmittedAtDate(OffsetDateTime.now())
        .withShortCode("shortCodeTest")
        .build();

    submissionRepositoryService.save(familySubmission);

   providerSubmission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("familySubmissionId", familySubmission.getId().toString())
        .build();

    submissionRepositoryService.save(providerSubmission);
  }

  @Test
  void ProviderSubmissionIsEnqueued() {
    uploadProviderSubmissionToS3 = new UploadProviderSubmissionToS3(
        pdfService,
        cloudFileRepository,
        pdfTransmissionJob,
        enqueueDocumentTransfer,
        submissionRepositoryService,
        userFileRepositoryService, uploadedDocumentTransmissionJob, s3PresignService);
    uploadProviderSubmissionToS3.run(providerSubmission);

    verify(enqueueDocumentTransfer, times(1)).enqueuePDFDocumentBySubmission(eq(pdfService), eq(cloudFileRepository),
        eq(pdfTransmissionJob), eq(familySubmission), any());
    verify(enqueueDocumentTransfer, times(1)).enqueueUploadedDocumentBySubmission(eq(userFileRepositoryService),
        eq(uploadedDocumentTransmissionJob), eq(s3PresignService), eq(familySubmission));

  }
}
