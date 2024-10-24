package org.ilgcc.app.submission.actions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.PdfTransmissionJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@SpringBootTest
class UploadProviderSubmissionToS3Test {

  @Autowired
  private SubmissionRepository submissionRepository;

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
        "true",
        submissionRepositoryService
    );
    uploadProviderSubmissionToS3.run(providerSubmission);

    verify(enqueueDocumentTransfer, times(1)).enqueuePDFDocumentBySubmission(eq(pdfService), eq(cloudFileRepository),
        eq(pdfTransmissionJob), eq(familySubmission), any());

  }
  @Test
  void whenFeatureFlagIsSetToFalseAndProviderSubmissionIsNotEnqueued(){
    uploadProviderSubmissionToS3 = new UploadProviderSubmissionToS3(
        pdfService,
        cloudFileRepository,
        pdfTransmissionJob,
        enqueueDocumentTransfer,
        "false",
        submissionRepositoryService
    );
    uploadProviderSubmissionToS3.run(providerSubmission);

    verify(enqueueDocumentTransfer, never()).enqueuePDFDocumentBySubmission(eq(pdfService), eq(cloudFileRepository),
        eq(pdfTransmissionJob), eq(familySubmission), any());
  }

  private String generateExpectedPdfPath(Submission submission) {
    return String.format("%s/%s.pdf", submission.getId(), submission.getId());
  }
}
