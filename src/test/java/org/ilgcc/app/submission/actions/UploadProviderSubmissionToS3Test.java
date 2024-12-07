package org.ilgcc.app.submission.actions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class UploadProviderSubmissionToS3Test {

  @MockBean
  private PdfService pdfService;

  @MockBean
  private CloudFileRepository cloudFileRepository;
  @MockBean
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
  @MockBean
  private UploadedDocumentTransmissionJob uploadedDocumentTransmissionJob;
  @MockBean
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
  void whenFeatureFlagIsSetToTrueAndProviderSubmissionIsEnqueued() {
    uploadProviderSubmissionToS3 = new UploadProviderSubmissionToS3(
        pdfService,
        cloudFileRepository,
        pdfTransmissionJob,
        enqueueDocumentTransfer,
        true,
        submissionRepositoryService,
        userFileRepositoryService, uploadedDocumentTransmissionJob, s3PresignService);
    uploadProviderSubmissionToS3.run(providerSubmission);

    verify(enqueueDocumentTransfer, times(1)).enqueuePDFDocumentBySubmission(eq(pdfService), eq(cloudFileRepository),
        eq(pdfTransmissionJob), eq(familySubmission), any());
    verify(enqueueDocumentTransfer, times(1)).enqueueUploadedDocumentBySubmission(eq(userFileRepositoryService),
        eq(uploadedDocumentTransmissionJob), eq(s3PresignService), eq(familySubmission));

  }
  @Test
  void whenFeatureFlagIsSetToFalseAndProviderSubmissionIsNotEnqueued(){
    uploadProviderSubmissionToS3 = new UploadProviderSubmissionToS3(
        pdfService,
        cloudFileRepository,
        pdfTransmissionJob,
        enqueueDocumentTransfer,
        false,
        submissionRepositoryService,
        userFileRepositoryService, uploadedDocumentTransmissionJob, s3PresignService);
    uploadProviderSubmissionToS3.run(providerSubmission);

    verify(enqueueDocumentTransfer, never()).enqueuePDFDocumentBySubmission(eq(pdfService), eq(cloudFileRepository),
        eq(pdfTransmissionJob), eq(familySubmission), any());
    verify(enqueueDocumentTransfer, never()).enqueueUploadedDocumentBySubmission(eq(userFileRepositoryService),
        eq(uploadedDocumentTransmissionJob), eq(s3PresignService), eq(familySubmission));
  }

  private String generateExpectedPdfPath(Submission submission) {
    return String.format("%s/%s.pdf", submission.getId(), submission.getId());
  }
}
