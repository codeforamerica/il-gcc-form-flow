package org.ilgcc.app;

import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForApplicationPDF;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.pdf.MultiProviderPDFService;
import org.ilgcc.app.utils.FileNameUtility;
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

    private final SubmissionRepositoryService submissionRepositoryService;

    private final MultiProviderPDFService pdfService;

    public MultipleProviderResponsePdfController(SubmissionRepositoryService submissionRepositoryService,
            MultiProviderPDFService pdfService) {
        this.submissionRepositoryService = submissionRepositoryService;
        this.pdfService = pdfService;
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

        log.info("Downloading PDF with provider submission_id: {} and family submission_id: {}", sanitizeString(submissionId), sanitizeString(familySubmission.getId().toString()));

        Map<String, byte[]> multiplePDFs = this.pdfService.generatePDFs(familySubmission);
        HttpHeaders headers = new HttpHeaders();
        if (multiplePDFs.keySet().size() > 1) {
            String zipFileName = FileNameUtility.getPDFFileNameZip(familySubmission);
            headers.add("Content-Disposition", "attachment; filename=" + zipFileName);
            headers.setContentType(MediaType.valueOf("application/zip"));
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(this.pdfService.zipped(multiplePDFs));

        } else {
            headers.add("Content-Disposition",
                    "attachment; filename=%s".formatted(getCCMSFileNameForApplicationPDF(familySubmission)));
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers).body(multiplePDFs.values().stream().findFirst());
        }

    }

    private String sanitizeString(String input) {
        return input.replaceAll("[^a-zA-Z0-9_-]", ""); // Allow only alphanumeric characters, hyphens, and underscores
    }
}
