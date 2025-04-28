package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import formflow.library.data.Submission;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@SpringBootTest
class UploadSubmissionToS3AndEnqueueCCMSPayloadTest {

  @MockitoBean
  private PdfService pdfService;

  @MockitoBean
  private CloudFileRepository cloudFileRepository;

  @Autowired
  private UploadSubmissionToS3AndEnqueueCCMSPayload uploadSubmissionToS3AndEnqueueCCMSPayload;

  private Submission submission;

  @BeforeEach
  void setUp() {
    submission = new Submission();
    submission.setId(UUID.randomUUID());
  }
  
  @Test
  void whenRun_thenPdfIsZippedAndUploadedToS3() throws IOException {

    byte[] pdfFiles = new byte[]{1, 2, 3, 4};
    when(pdfService.getFilledOutPDF(submission)).thenReturn(pdfFiles);
    submission.setSubmittedAt(OffsetDateTime.now());
    submission.getInputData().put("hasChosenProvider","false");

    uploadSubmissionToS3AndEnqueueCCMSPayload.run(submission);

    await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
      verify(pdfService).getFilledOutPDF(submission);
      verify(cloudFileRepository).upload(eq(generateExpectedPdfPath(submission)), any(MultipartFile.class));
    });
  }

  @Test
  void setsExpirationDateAndStatusWhenProviderIsChosen() throws IOException {

    byte[] pdfFiles = new byte[]{1, 2, 3, 4};
    when(pdfService.getFilledOutPDF(submission)).thenReturn(pdfFiles);
    submission.setSubmittedAt(OffsetDateTime.now());
    submission.getInputData().put("hasChosenProvider","true");

    assertThat(submission.getInputData().get("providerApplicationResponseStatus")).isNull();
    assertThat(submission.getInputData().get("providerApplicationResponseExpirationDate")).isNull();

    uploadSubmissionToS3AndEnqueueCCMSPayload.run(submission);

    await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
      verifyNoInteractions(pdfService, cloudFileRepository);
    });

    assertThat(submission.getInputData().get("providerApplicationResponseStatus")).isEqualTo("ACTIVE");
    assertThat(submission.getInputData().get("providerApplicationResponseExpirationDate")).isNotNull();
  }

  private String generateExpectedPdfPath(Submission submission) {
    return String.format("%s/%s.pdf", submission.getId(), submission.getId());
  }
}
