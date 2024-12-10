package org.ilgcc.app.submission.actions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import formflow.library.data.Submission;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.io.IOException;
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
class UploadSubmissionToS3Test {

  @MockitoBean
  private PdfService pdfService;

  @MockitoBean
  private CloudFileRepository cloudFileRepository;

  @Autowired
  private UploadSubmissionToS3 uploadSubmissionToS3;

  private Submission submission;

  @BeforeEach
  void setUp() {
    submission = new Submission();
    submission.setId(UUID.randomUUID());
  }

  @Test
  void whenRun_thenPdfIsZippedAndUploadedToS3() throws IOException, InterruptedException {

    byte[] pdfFiles = new byte[]{1, 2, 3, 4};
    when(pdfService.getFilledOutPDF(submission)).thenReturn(pdfFiles);
    submission.setSubmittedAt(OffsetDateTime.now());
    uploadSubmissionToS3.run(submission);

    verify(pdfService).getFilledOutPDF(submission);
    verify(cloudFileRepository).upload(eq(generateExpectedPdfPath(submission)), any(MultipartFile.class));
  }

  private String generateExpectedPdfPath(Submission submission) {
    return String.format("%s/%s.pdf", submission.getId(), submission.getId());
  }
}
