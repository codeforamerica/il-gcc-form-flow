package org.ilgcc.app;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.PdfService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping({"/provider-response-download"})
public class ProviderResponsePdfController {

    private final PdfService pdfService;
    private final SubmissionRepositoryService submissionRepositoryService;

    public ProviderResponsePdfController(PdfService pdfService, SubmissionRepositoryService submissionRepositoryService) {
        this.pdfService = pdfService;
        this.submissionRepositoryService = submissionRepositoryService;
    }


    @GetMapping({"{flow}/{submissionId}"})
    ResponseEntity<?> downloadPdf(@PathVariable String flow, @PathVariable String submissionId, HttpServletRequest request) throws IOException {
        log.info("GET downloadPdf (url: {}): flow: {}, submissionId: {}", sanitizeString(request.getRequestURI().toLowerCase()),
                sanitizeString(flow),
                sanitizeString(submissionId));

        Optional<Submission> optionalProviderSubmission = this.submissionRepositoryService.findById(
                UUID.fromString(submissionId));
        if (optionalProviderSubmission.isEmpty()) {
            log.error("Attempted to download PDF with provider submission id: {} but no submission was found",
                    sanitizeString(submissionId));
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Attempted to download PDF with provider submission id: %s but no submission was found",
                            sanitizeString(submissionId)));
        }

        Submission providerSubmission = optionalProviderSubmission.get();
        String providerResponseFamilyShortCode = providerSubmission.getInputData()
                .getOrDefault("providerResponseFamilyShortCode", null).toString();

        if (providerResponseFamilyShortCode == null) {
            log.error(
                    "Attempted to download PDF with provider submission id: {} but no providerResponseFamilyShortCode was found",
                    sanitizeString(submissionId));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format(
                    "Attempted to download PDF with provider submission id: %s but no providerResponseFamilyShortCode was found.",
                    sanitizeString(submissionId)));
        }

        Optional<Submission> optionalFamilySubmission = submissionRepositoryService.findByShortCode(
                providerResponseFamilyShortCode);
        if (optionalFamilySubmission.isEmpty()) {
            log.error(
                    "Attempted to download PDF with provider submission id: {} but no family submission was found with confirmation code: {}",
                    sanitizeString(submissionId), sanitizeString(providerResponseFamilyShortCode));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format(
                    "Attempted to download PDF with provider submission id: %s but no family submission was found with confirmation code: %s",
                    sanitizeString(submissionId), sanitizeString(providerResponseFamilyShortCode)));
        }

        Submission familySubmission = optionalFamilySubmission.get();
        // This will always set the current provider app as the provider app for the family application
        // Note that many provider apps can be tied to a single family application
        // This is only for testing purposes in the provider app in dev and staging
        familySubmission.getInputData().put("providerResponseSubmissionId", submissionId);
        submissionRepositoryService.save(familySubmission);


        log.info("Downloading PDF with provider submission_id: {} and family submission_id: {}", sanitizeString(submissionId),
                sanitizeString(String.valueOf(familySubmission.getId())));
        HttpHeaders headers = new HttpHeaders();
        byte[] data = this.pdfService.getFilledOutPDF(familySubmission);
        headers.add("Content-Disposition",
                "attachment; filename=%s".formatted(this.pdfService.generatePdfName(familySubmission)));
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers).body(data);
    }

    private String sanitizeString(String input) {
        return input.replaceAll("[^a-zA-Z0-9_-]", ""); // Allow only alphanumeric characters, hyphens, and underscores
    }
}
