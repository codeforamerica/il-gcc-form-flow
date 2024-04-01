package org.ilgcc.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.file.CloudFileRepository;
import formflow.library.file.S3CloudFileRepository;
import formflow.library.pdf.PdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class UploadSubmissionToS3Test {

  @MockBean
  private PdfService pdfService;

  @MockBean
  private CloudFileRepository cloudFileRepository;

  @Autowired
  private UploadSubmissionToS3 uploadSubmissionToS3;

  private Submission submission;
  private FormSubmission formSubmission;

  @BeforeEach
  void setUp() {
    submission = new Submission();
    submission.setId(UUID.randomUUID());
    formSubmission = new FormSubmission(Map.of());
  }

  @Test
  void whenRun_thenPdfIsZippedAndUploadedToS3() throws IOException, InterruptedException {

    byte[] pdfFiles = new byte[]{1, 2, 3, 4};
    when(pdfService.getFilledOutPDF(submission)).thenReturn(pdfFiles);

    uploadSubmissionToS3.run(formSubmission, submission);

    verify(pdfService).getFilledOutPDF(submission);
    verify(cloudFileRepository).upload(eq(generateExpectedZipPath(submission)), any(MultipartFile.class));
  }

  private String generateExpectedZipPath(Submission submission) {
    return String.format("%s/%s.zip", submission.getId(), submission.getId());
  }
}
