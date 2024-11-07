package org.ilgcc.app;

import static formflow.library.FormFlowController.getSubmissionIdForFlow;

import formflow.library.FormFlowController;
import formflow.library.config.FlowConfiguration;
import formflow.library.config.FormFlowConfigurationProperties;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.pdf.PdfService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
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
    private final MessageSource messageSource;

    public ProviderResponsePdfController(PdfService pdfService, SubmissionRepositoryService submissionRepositoryService,
            MessageSource messageSource) {
        this.pdfService = pdfService;
        this.submissionRepositoryService = submissionRepositoryService;
        this.messageSource = messageSource;
    }


    @GetMapping({"{flow}/{submissionId}"})
    ResponseEntity<?> downloadPdf(@PathVariable String flow, @PathVariable String submissionId, HttpSession httpSession, HttpServletRequest request, Locale locale) throws IOException {
        log.info("GET downloadPdf (url: {}): flow: {}, submissionId: {}", request.getRequestURI().toLowerCase(), flow,
                submissionId);

        Optional<Submission> optionalProviderSubmission = this.submissionRepositoryService.findById(UUID.fromString(submissionId));
        if (optionalProviderSubmission.isEmpty()) {
            log.error("Attempted to download PDF with submission_id: {} but no submission was found", submissionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.messageSource.getMessage("error.notfound", null, locale));
        }

        Submission providerSubmission = optionalProviderSubmission.get();
        String providerResponseFamilyConfirmationCode = providerSubmission.getInputData()
                .getOrDefault("providerResponseFamilyConfirmationCode", null).toString();
        
        if (providerResponseFamilyConfirmationCode == null) {
            log.error("Attempted to download PDF with submission_id: {} but no providerResponseFamilyConfirmationCode was found",
                    submissionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.messageSource.getMessage("error.notfound", null, locale));
        }

        Optional<Submission> optionalFamilySubmission = submissionRepositoryService.findByShortCode(providerResponseFamilyConfirmationCode);
        if (optionalFamilySubmission.isEmpty()) {
            log.error("Attempted to download PDF with submission_id: {} but no family submission was found with confirmation code: {}",
                    submissionId, providerResponseFamilyConfirmationCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.messageSource.getMessage("error.notfound", null, locale));
        }
        
        Submission familySubmission = optionalFamilySubmission.get();

        log.info("Downloading PDF with provider submission_id: {} and family submission_id: {}", submissionId,
                familySubmission.getId());
        HttpHeaders headers = new HttpHeaders();
        byte[] data = this.pdfService.getFilledOutPDF(familySubmission);
        headers.add("Content-Disposition", "attachment; filename=%s".formatted(this.pdfService.generatePdfName(familySubmission)));
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers).body(data);
    }
}
