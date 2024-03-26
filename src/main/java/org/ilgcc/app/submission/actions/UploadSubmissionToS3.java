package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.file.S3CloudFileRepository;
import formflow.library.pdf.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ByteArrayMultipartFile;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
public class UploadSubmissionToS3 implements Action {

  private final PdfService pdfService;
  private final S3CloudFileRepository s3CloudFileRepository;
  private final String CONTENT_TYPE = "application/pdf";



  public UploadSubmissionToS3(PdfService pdfService, S3CloudFileRepository s3CloudFileRepository) {
    this.pdfService = pdfService;
    this.s3CloudFileRepository = s3CloudFileRepository;
  }

  @Override
  public void run(FormSubmission formSubmission, Submission submission) {

    try {
      var file = pdfService.getFilledOutPDF(submission);
      MultipartFile multipartFile = new ByteArrayMultipartFile(file, String.format("%s.pdf", submission.getId()), CONTENT_TYPE);
      s3CloudFileRepository.upload(generatePdfPath(submission), multipartFile);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public String generatePdfPath(Submission submission) {
    return String.format("%s/%s.pdf", submission.getId(), submission.getId());
  }
}
