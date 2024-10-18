package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.links.NoOpLinkShortener;
import org.ilgcc.app.links.ShortLinkService;
import org.ilgcc.app.links.ShortenedLinks;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Component
public class GenerateShortLinks implements Action {

    private final SubmissionRepositoryService submissionRepositoryService;
    private final HttpServletRequest httpRequest;

    @Value("${il-gcc.dts.wait-for-provider-response}")
    boolean waitForProviderResponse;

    private static final String SUBMISSION_DATA_EMAIL_LINK = "emailLink";
    private static final String SUBMISSION_DATA_TEXT_LINK = "textLink";
    private static final String SUBMISSION_DATA_CLIPBOARD_LINK = "clipboardLink";

    public GenerateShortLinks(HttpServletRequest httpRequest, SubmissionRepositoryService submissionRepositoryService) {
        this.submissionRepositoryService = submissionRepositoryService;
        this.httpRequest = httpRequest;
    }

    @Override
    public void run(Submission submission) {

        String baseUrl = ServletUriComponentsBuilder.fromContextPath(httpRequest).build().toUriString();

        String emailUrl = SubmissionUtilities.convertToAbsoluteURLForEmailAndText(submission.getShortCode(), "email", baseUrl);
        String textUrl = SubmissionUtilities.convertToAbsoluteURLForEmailAndText(submission.getShortCode(), "text", baseUrl);
        String clipboardUrl = SubmissionUtilities.convertToAbsoluteURLForEmailAndText(submission.getShortCode(), "copy_link",
                baseUrl);

        submission.getInputData().put(SUBMISSION_DATA_EMAIL_LINK, emailUrl);
        submission.getInputData().put(SUBMISSION_DATA_TEXT_LINK, textUrl);
        submission.getInputData().put(SUBMISSION_DATA_CLIPBOARD_LINK, clipboardUrl);

        if (waitForProviderResponse) {
            CompletableFuture<ShortenedLinks> shortenedLinks = CompletableFuture.supplyAsync(() -> {
                ShortLinkService shortLinkService = new NoOpLinkShortener();
                ShortenedLinks shortUrls = shortLinkService.getShortLinks(emailUrl, textUrl, clipboardUrl);
                log.info("Shortened email url of {} to be {}", emailUrl, shortUrls.getShortEmailLink());
                log.info("Shortened text url of {} to be {}", textUrl, shortUrls.getShortTextLink());
                log.info("Shortened clipboard url of {} to be {}", clipboardUrl, shortUrls.getShortClipboardLink());
                return shortUrls;
            }).exceptionally(e -> {
                log.error("Error shortening links for submission {}", submission.getId(), e);
                return new ShortenedLinks(emailUrl, textUrl, clipboardUrl);
            });

            shortenedLinks.thenAccept(links -> {
                submission.getInputData().put(SUBMISSION_DATA_EMAIL_LINK, links.getShortEmailLink());
                submission.getInputData().put(SUBMISSION_DATA_TEXT_LINK, links.getShortTextLink());
                submission.getInputData().put(SUBMISSION_DATA_CLIPBOARD_LINK, links.getShortClipboardLink());
                submissionRepositoryService.save(submission);
            });
        } else {
            log.debug("Short link generation turned off.");
        }
    }
}