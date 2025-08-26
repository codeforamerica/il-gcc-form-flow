package org.ilgcc.app;

import static org.ilgcc.app.utils.FileNameUtility.getCCMSFileNameForApplicationPDF;
import static org.ilgcc.app.utils.TextUtilities.sanitize;

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
import org.springframework.http.HttpStatus;
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

        if (log.isDebugEnabled()) {
            log.debug("GET downloadPdfZip (url: {}): flow: {}, submissionId: {}",
                    sanitize(request.getRequestURI().toLowerCase()),
                    sanitize(flow),
                    sanitize(submissionId));
        }

        final Optional<Submission> optionalFamilySubmission = submissionRepositoryService.findById(UUID.fromString(submissionId));
        ResponseEntity response = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (optionalFamilySubmission.isPresent()) {
            final Submission familySubmission = optionalFamilySubmission.get();
            final Map<String, byte[]> multiplePDFs = this.pdfService.generatePDFs(familySubmission);
            final HttpHeaders headers = new HttpHeaders();

            if (log.isDebugEnabled()) {
                log.debug("Downloading PDF with provider submission_id: {} and family submission_id: {}",
                        sanitize(submissionId),
                        sanitize(familySubmission.getId().toString()));
            }

            if (multiplePDFs.keySet().size() > 1) {
                final String zipFileName = FileNameUtility.getPDFFileNameZip(familySubmission);
                headers.add("Content-Disposition", "attachment; filename=" + zipFileName);
                headers.setContentType(MediaType.valueOf("application/zip"));
                response = ResponseEntity.ok()
                        .headers(headers)
                        .body(this.pdfService.zipped(multiplePDFs));

            } else {
                final String fileNameKey = getCCMSFileNameForApplicationPDF(familySubmission);
                headers.add("Content-Disposition",
                        "attachment; filename=%s".formatted(fileNameKey));
                response = ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers)
                        .body(multiplePDFs.get(fileNameKey));
            }
            return response;
        }

        if (log.isDebugEnabled()) {
            log.debug("Attempted to download PDF with family submission id: {} but no submission was found",
                    sanitize(submissionId));
        }

        return response;
    }
}
