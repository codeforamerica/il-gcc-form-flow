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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class UploadSubmissionToS3 implements Action {

  private final PdfService pdfService;
  private final S3CloudFileRepository s3CloudFileRepository;
  private final String CONTENT_TYPE = "application/zip";


  public UploadSubmissionToS3(PdfService pdfService, S3CloudFileRepository s3CloudFileRepository) {
    this.pdfService = pdfService;
    this.s3CloudFileRepository = s3CloudFileRepository;
  }

  @Override
  public void run(FormSubmission formSubmission, Submission submission) {
    try {

      byte[] pdfFile = pdfService.getFilledOutPDF(submission);
      String pdfFileName = String.format("%s.pdf", submission.getId());

      try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
           ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {


        ZipEntry zipEntry = new ZipEntry(pdfFileName);
        zipOutputStream.putNextEntry(zipEntry);
        zipOutputStream.write(pdfFile);
        zipOutputStream.closeEntry();

        zipOutputStream.finish();

        byte[] zipBytes = byteArrayOutputStream.toByteArray();
        MultipartFile multipartFile = new ByteArrayMultipartFile(zipBytes, String.format("%s.zip", submission.getId()), CONTENT_TYPE);

        s3CloudFileRepository.upload(generateZipPath(submission), multipartFile);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String generateZipPath(Submission submission) {
    return String.format("%s/%s.zip", submission.getId(), submission.getId());
  }
}
