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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component
public class UploadSubmissionToS3 implements Action {

  private final PdfService pdfService;
  private final CloudFileRepository cloudFileRepository;
  private final String CONTENT_TYPE = "application/zip";


  public UploadSubmissionToS3(PdfService pdfService, CloudFileRepository cloudFileRepository) {
    this.pdfService = pdfService;
    this.cloudFileRepository = cloudFileRepository;
  }

  @Override
  public void run(FormSubmission formSubmission, Submission submission) {

    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
        byte[] pdfFile = pdfService.getFilledOutPDF(submission);
        String pdfFileName = String.format("%s.pdf", submission.getId());

        ZipEntry zipEntry = new ZipEntry(pdfFileName);
        zipOutputStream.putNextEntry(zipEntry);
        zipOutputStream.write(pdfFile);
        zipOutputStream.closeEntry();

        zipOutputStream.finish();

        byte[] zipBytes = byteArrayOutputStream.toByteArray();
        MultipartFile multipartFile = new ByteArrayMultipartFile(zipBytes, String.format("%s.zip", submission.getId()), CONTENT_TYPE);

        cloudFileRepository.upload(generateZipPath(submission), multipartFile);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public String generateZipPath(Submission submission) {
    return String.format("%s/%s.zip", submission.getId(), submission.getId());
  }
}
