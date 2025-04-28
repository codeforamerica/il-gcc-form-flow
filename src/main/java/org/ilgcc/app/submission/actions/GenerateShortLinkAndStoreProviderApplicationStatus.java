package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.SubmissionUtilities.hasNotChosenProvider;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Component
public class GenerateShortLinkAndStoreProviderApplicationStatus implements Action {

    private final HttpServletRequest httpRequest;

    private static final String SUBMISSION_DATA_SHAREABLE_LINK = "shareableLink";

    private static final String PROVIDER_APPLICATION_STATUS = "providerApplicationResponseStatus";

    private static final String PROVIDER_APPLICATION_EXPIRATION = "providerApplicationResponseExpirationDate";


    public GenerateShortLinkAndStoreProviderApplicationStatus(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public void run(Submission submission) {
        String baseUrl = ServletUriComponentsBuilder.fromContextPath(httpRequest).build().toUriString();

        String shareableLink = SubmissionUtilities.convertToAbsoluteURLForEmailAndText(submission.getShortCode(), baseUrl);

        submission.getInputData().put(SUBMISSION_DATA_SHAREABLE_LINK, shareableLink);

        submission.getInputData().put(PROVIDER_APPLICATION_STATUS, SubmissionStatus.ACTIVE.name());

        submission.getInputData().put(PROVIDER_APPLICATION_EXPIRATION, expirationDate(submission));
    }

    private ZonedDateTime expirationDate(Submission submission) {
        if (hasNotChosenProvider(submission)) {
            return submission.getSubmittedAt().atZoneSameInstant(ZoneId.of("America/Chicago"));
        } else {
            return ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submission.getSubmittedAt());
        }
    }
}