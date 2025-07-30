package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getProviderSubmissionDataForEmails;
import static org.ilgcc.app.utils.SubmissionUtilities.isNoProviderSubmission;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.DateUtilities;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SchedulePreparerUtility;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Component
public class GenerateShortLinkAndStoreProviderApplicationStatus implements Action {

    private final HttpServletRequest httpRequest;

    private boolean enableFasterApplicationExpiry;
    private int fasterApplicationExpiryMinutes;

    private static final String SUBMISSION_DATA_SHAREABLE_LINK = "shareableLink";

    private static final String PROVIDER_APPLICATION_STATUS = "providerApplicationResponseStatus";

    private static final String PROVIDER_APPLICATION_EXPIRATION = "providerApplicationResponseExpirationDate";

    public GenerateShortLinkAndStoreProviderApplicationStatus(HttpServletRequest httpRequest,
            @Value("${il-gcc.enable-faster-application-expiry}") boolean enableFasterApplicationExpiry,
            @Value("${il-gcc.enable-faster-application-expiry-minutes}") int fasterApplicationExpiryMinutes) {
        this.httpRequest = httpRequest;
        this.enableFasterApplicationExpiry = enableFasterApplicationExpiry;
        this.fasterApplicationExpiryMinutes = fasterApplicationExpiryMinutes;
    }

    @Override
    public void run(Submission submission) {
        String baseUrl = ServletUriComponentsBuilder.fromContextPath(httpRequest).build().toUriString();

        String shareableLink = SubmissionUtilities.convertToAbsoluteURLForEmailAndText(submission.getShortCode(), baseUrl);

        submission.getInputData().put(SUBMISSION_DATA_SHAREABLE_LINK, shareableLink);

        submission.getInputData().put(PROVIDER_APPLICATION_EXPIRATION, getExpirationDate(submission,
                isNoProviderSubmission(submission.getInputData())));

        submission.getInputData()
                .put(PROVIDER_APPLICATION_STATUS, getApplicationStatus(isNoProviderSubmission(submission.getInputData())));

        if (submission.getInputData().containsKey("providers")) {
            submission.getInputData().replace("providers", providersWithApplicationStatuses(submission.getInputData()));
        }

    }

    private List<Map<String, Object>> providersWithApplicationStatuses(Map<String, Object> familyInputData) {
        List<Map<String, Object>> providers = (List<Map<String, Object>>) familyInputData
                .getOrDefault("providers", Collections.EMPTY_LIST);

        Map<String, List<Map<String, Object>>> providerSchedules =
                (Map<String, List<Map<String, Object>>>) SchedulePreparerUtility.getRelatedChildrenSchedulesForEachProvider(
                        familyInputData);

        for (Map<String, Object> provider : providers) {
            if (providerSchedules.containsKey(provider.get("uuid"))) {
                provider.put(PROVIDER_APPLICATION_STATUS, SubmissionStatus.ACTIVE.name());
            } else {
                provider.put(PROVIDER_APPLICATION_STATUS, SubmissionStatus.INACTIVE.name());
            }
        }

        return providers;
    }

    private ZonedDateTime getExpirationDate(Submission submission, Boolean noProviderApplication) {
        if (noProviderApplication) {
            return submission.getSubmittedAt().atZoneSameInstant(ZoneId.of("America/Chicago"));
        } else if (enableFasterApplicationExpiry) {
            return submission.getSubmittedAt().plusMinutes(this.fasterApplicationExpiryMinutes)
                    .atZoneSameInstant(ZoneId.of("America/Chicago"));
        } else {
            return ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submission.getSubmittedAt());
        }
    }

    private String getApplicationStatus(Boolean noProviderApplication) {
        if (noProviderApplication) {
            return SubmissionStatus.INACTIVE.name();
        } else {
            return SubmissionStatus.ACTIVE.name();
        }
    }
}