package org.ilgcc.app;

import formflow.library.FileController;
import formflow.library.data.Submission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Locale;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.Transaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * A controller that checks if the document being uploaded has already been sent to be processed,
 * and if so, stops the upload and lets the user know. Otherwise, it falls through to the FFL's
 * FileController for further validation and processing.
 */
@Controller
@EnableAutoConfiguration
@Slf4j
public class DocumentUploadController {

    private final TransactionRepositoryService transactionRepositoryService;
    private final FileController fileController;
    protected final MessageSource messageSource;

    public DocumentUploadController(FileController fileController,
            TransactionRepositoryService transactionRepositoryService,
            MessageSource messageSource) {

        this.fileController = fileController;
        this.transactionRepositoryService = transactionRepositoryService;
        this.messageSource = messageSource;
    }

    /**
     * Document upload endpoint.
     *
     * @param file         A MultipartFile file
     * @param flow         The current flow name
     * @param inputName    The current inputName
     * @param thumbDataUrl The thumbnail URL generated from the upload
     * @param httpSession  The current HTTP session
     * @return ON SUCCESS: ResponseEntity with a body containing the id of a file. body.
     * <p>ON FAILURE: ResponseEntity with an error message and a status code.</p>
     */
    @PostMapping(value = "/doc-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("flow") String flow,
            @RequestParam("inputName") String inputName,
            @RequestParam("thumbDataURL") String thumbDataUrl,
            @RequestParam("screen") String screen,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) {
        log.debug("POST doc-upload (url: {}): flow: {} inputName: {}", request.getRequestURI().toLowerCase(), sanitize(flow), sanitize(inputName));

        Submission submission = fileController.findOrCreateSubmission(httpSession, flow);

        if (submission != null) {

            UUID submissionId;
            String familySubmissionId = (String)submission.getInputData().get("familySubmissionId");
            if (familySubmissionId != null) {
                // If we have a familySubmissionId, this means we're uploading to a provider submission
                // Since we only send the family submission to CCMS, we need to check against the database
                // table for a transaction using this UUID and not the provider submission's id.
                submissionId = UUID.fromString(familySubmissionId);
            } else {
                submissionId = submission.getId();
            }

            if (submissionId != null) {
                Transaction transaction = transactionRepositoryService.getBySubmissionId(submission.getId());
                if (transaction != null) {
                    // The submission was already sent to CCMS
                    log.info("Submission {} was already sent to CCMS.", submission.getId());
                    String message = messageSource.getMessage("doc-upload-add-files.error.already-sent", null, locale);
                    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
                }
            }
        }

        return fileController.upload(file, flow, inputName, thumbDataUrl, screen, httpSession, request, locale);
    }

    private String sanitize(String string) {
        return string.replaceAll("[^a-zA-Z0-9]", "*").replaceAll("\n", "*").replaceAll("\r", "*");
    }
}