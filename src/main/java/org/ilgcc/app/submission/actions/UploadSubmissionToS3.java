package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ByteArrayMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Slf4j
@Component
public class UploadSubmissionToS3 implements Action {

  private final PdfService pdfService;
  private final CloudFileRepository cloudFileRepository;
  private final String CONTENT_TYPE = "application/pdf";


  public UploadSubmissionToS3(PdfService pdfService, CloudFileRepository cloudFileRepository) {
    this.pdfService = pdfService;
    this.cloudFileRepository = cloudFileRepository;
  }

  @Override
  public void run(FormSubmission formSubmission, Submission submission) {

    try {
        byte[] pdfFile = pdfService.getFilledOutPDF(submission);
        String pdfFileName = String.format("%s.pdf", submission.getId());

        MultipartFile multipartFile = new ByteArrayMultipartFile(pdfFile, pdfFileName, CONTENT_TYPE);

        cloudFileRepository.upload(generatePdfPath(submission), multipartFile);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public String generatePdfPath(Submission submission) {
    return String.format("%s/%s.pdf", submission.getId(), submission.getId());
  }
}
