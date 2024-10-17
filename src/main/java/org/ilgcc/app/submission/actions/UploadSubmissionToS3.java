package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.links.NoOpLinkShortener;
import org.ilgcc.app.links.ShortLinkService;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.ilgcc.jobs.EnqueueDocumentTransfer;
import org.ilgcc.jobs.PdfTransmissionJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Component
public class UploadSubmissionToS3 implements Action {

    private final PdfService pdfService;
    private final CloudFileRepository cloudFileRepository;
    private final SubmissionRepositoryService submissionRepositoryService;

    private final PdfTransmissionJob pdfTransmissionJob;

    private final EnqueueDocumentTransfer enqueueDocumentTransfer;
    
    private final String waitForProviderResponseFlag;

    private final HttpServletRequest httpRequest;

    private static final String SUBMISSION_DATA_EMAIL_LINK = "emailLink";
    private static final String SUBMISSION_DATA_TEXT_LINK = "textLink";
    private static final String SUBMISSION_DATA_CLIPBOARD_LINK = "clipboardLink";

    public UploadSubmissionToS3(HttpServletRequest httpRequest, PdfService pdfService, CloudFileRepository cloudFileRepository,
            SubmissionRepositoryService submissionRepositoryService, PdfTransmissionJob pdfTransmissionJob,
            EnqueueDocumentTransfer enqueueDocumentTransfer,
            @Value("${il-gcc.dts.wait-for-provider-response}") String waitForProviderResponseFlag) {

        this.pdfService = pdfService;
        this.cloudFileRepository = cloudFileRepository;
        this.submissionRepositoryService = submissionRepositoryService;
        this.pdfTransmissionJob = pdfTransmissionJob;
        this.enqueueDocumentTransfer = enqueueDocumentTransfer;
        this.waitForProviderResponseFlag = waitForProviderResponseFlag;
        this.httpRequest = httpRequest;
    }

    @Override
    public void run(Submission submission) {
        if (waitForProviderResponseFlag.equals("false")) {
            enqueueDocumentTransfer.enqueuePDFDocumentBySubmission(pdfService, cloudFileRepository, pdfTransmissionJob,
                    submission, FileNameUtility.getFileNameForPdf(submission));
        }

        if (true) {
            // TODO: flag here to turn on link shortening and call real shortener
            String baseUrl = ServletUriComponentsBuilder.fromContextPath(httpRequest).build().toUriString();

            String emailUrl = SubmissionUtilities.convertToAbsoluteURLForEmailAndText(submission.getShortCode(), "email",
                    baseUrl);
            String textUrl = SubmissionUtilities.convertToAbsoluteURLForEmailAndText(submission.getShortCode(), "text",
                    baseUrl);
            String clipboardUrl = SubmissionUtilities.convertToAbsoluteURLForEmailAndText(submission.getShortCode(),
                    "copy_link", baseUrl);

            submission.getInputData().put(SUBMISSION_DATA_EMAIL_LINK, emailUrl);
            submission.getInputData().put(SUBMISSION_DATA_TEXT_LINK, textUrl);
            submission.getInputData().put(SUBMISSION_DATA_CLIPBOARD_LINK, clipboardUrl);
            submissionRepositoryService.save(submission);

            CompletableFuture<String> shortEmailUrl = CompletableFuture.supplyAsync(() -> {
                ShortLinkService shortLinkService = new NoOpLinkShortener();
                String shortUrl = shortLinkService.getShortLink(emailUrl);
                log.info("Shortened email url of {} to be {}", emailUrl, shortUrl);
                return shortUrl;
            }).exceptionally(e -> {
                log.error("Error shortening email link for submission {}", submission.getId(), e);
                return emailUrl;
            });

            shortEmailUrl.thenAccept(url -> {
                submission.getInputData().put(SUBMISSION_DATA_EMAIL_LINK, url);
                submissionRepositoryService.save(submission);
            });

            CompletableFuture<String> shortTextUrl = CompletableFuture.supplyAsync(() -> {
                ShortLinkService shortLinkService = new NoOpLinkShortener();
                String shortUrl = shortLinkService.getShortLink(textUrl);
                log.info("Shortened text url of {} to be {}", textUrl, shortUrl);
                return shortUrl;
            }).exceptionally(e -> {
                log.error("Error shortening text link for submission {}", submission.getId(), e);
                return null;
            });

            shortTextUrl.thenAccept(url -> {
                submission.getInputData().put(SUBMISSION_DATA_TEXT_LINK, url);
                submissionRepositoryService.save(submission);
            });

            CompletableFuture<String> shortClipboardUrl = CompletableFuture.supplyAsync(() -> {
                ShortLinkService shortLinkService = new NoOpLinkShortener();
                String shortUrl = shortLinkService.getShortLink(clipboardUrl);
                log.info("Shortened text url of {} to be {}", clipboardUrl, shortUrl);
                return shortUrl;
            }).exceptionally(e -> {
                log.error("Error shortening clipboard link for submission {}", submission.getId(), e);
                return null;
            });

            shortClipboardUrl.thenAccept(url -> {
                submission.getInputData().put(SUBMISSION_DATA_CLIPBOARD_LINK, url);
                submissionRepositoryService.save(submission);
            });
        }
    }
}