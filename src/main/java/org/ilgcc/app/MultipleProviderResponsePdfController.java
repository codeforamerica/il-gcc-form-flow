package org.ilgcc.app;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.PdfService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.pdf.ILGCCAPPDFService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping({"/download-zip"})
public class MultipleProviderResponsePdfController {

    private final PdfService pdfService;

    private final SubmissionRepositoryService submissionRepositoryService;

    private final ILGCCAPPDFService ilgccappdfService;

    private final Boolean enableMultipleProviders;

    public MultipleProviderResponsePdfController(PdfService pdfService, SubmissionRepositoryService submissionRepositoryService,
            ILGCCAPPDFService ilgccappdfService, @Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.pdfService = pdfService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.ilgccappdfService = ilgccappdfService;
        this.enableMultipleProviders = enableMultipleProviders;
    }


    @GetMapping({"{flow}/{submissionId}"})
    ResponseEntity<?> downloadPdf(@PathVariable String flow, @PathVariable String submissionId, HttpServletRequest request)
            throws IOException {
        log.info("GET downloadPdfZip (url: {}): flow: {}, submissionId: {}",
                sanitizeString(request.getRequestURI().toLowerCase()),
                sanitizeString(flow),
                sanitizeString(submissionId));

        Optional<Submission> optionalFamilySubmission = submissionRepositoryService.findById(UUID.fromString(submissionId));
        if (optionalFamilySubmission.isEmpty()) {
            log.warn("Attempted to download PDF with family submission id: {} but no submission was found",
                    sanitizeString(submissionId));
        }

        Submission familySubmission = optionalFamilySubmission.get();

        log.info("Downloading PDF with provider submission_id: {} and family submission_id: {}", sanitizeString(submissionId));

        Map<String, byte[]> multiplePDFs = this.ilgccappdfService.generatePDFs(familySubmission);
        HttpHeaders headers = new HttpHeaders();
        if (enableMultipleProviders && multiplePDFs.keySet().size() > 1) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (Map.Entry<String, byte[]> entry : multiplePDFs.entrySet()) {
                    String fileName = entry.getKey();
                    byte[] fileContent = entry.getValue();

                    ZipEntry zipEntry = new ZipEntry(fileName);
                    zos.putNextEntry(zipEntry);
                    zos.write(fileContent);
                    zos.closeEntry();
                }
                zos.finish();
            }

            byte[] zipBytes = baos.toByteArray();
            String zipFileName = this.pdfService.generatePdfName(familySubmission).replace(".pdf", ".zip");

            headers.add("Content-Disposition", "attachment; filename=" + zipFileName);
            headers.setContentType(MediaType.valueOf("application/zip"));
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipBytes);

        } else {
            byte[] data = this.pdfService.getFilledOutPDF(familySubmission);
            headers.add("Content-Disposition",
                    "attachment; filename=%s".formatted(this.pdfService.generatePdfName(familySubmission)));
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers).body(data);
        }

    }

    private String sanitizeString(String input) {
        return input.replaceAll("[^a-zA-Z0-9_-]", ""); // Allow only alphanumeric characters, hyphens, and underscores
    }
}
