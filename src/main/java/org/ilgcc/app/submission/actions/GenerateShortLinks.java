package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Component
public class GenerateShortLinks implements Action {

    private final HttpServletRequest httpRequest;

    private static final String SUBMISSION_DATA_SHAREABLE_LINK = "shareableLink";

    public GenerateShortLinks(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public void run(Submission submission) {
        String baseUrl = ServletUriComponentsBuilder.fromContextPath(httpRequest).build().toUriString();

        String shareableLink = SubmissionUtilities.convertToAbsoluteURLForEmailAndText(submission.getShortCode(), baseUrl);

        submission.getInputData().put(SUBMISSION_DATA_SHAREABLE_LINK, shareableLink);
    }
}