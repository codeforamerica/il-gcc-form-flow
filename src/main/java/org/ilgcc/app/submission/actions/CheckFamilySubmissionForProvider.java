package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.utils.ProviderSubmissionUtilities.providerApplicationHasExpired;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_CONFIRMATION_CODE;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_SUBMISSION_ID;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_PROVIDER_SUBMISSION_STATUS;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_SELECTED_PROVIDER_NAME;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.enums.ProviderSubmissionStatus;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckFamilySubmissionForProvider implements Action {

    private final HttpSession httpSession;
    private final MessageSource messageSource;
    private final SubmissionRepositoryService submissionRepositoryService;

    public CheckFamilySubmissionForProvider(HttpSession httpSession, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        this.httpSession = httpSession;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission submission) {

        UUID familySubmissionId = (UUID) httpSession.getAttribute(SESSION_KEY_FAMILY_SUBMISSION_ID);
        String providerSubmissionStatus = (String) httpSession.getAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS);

        if (familySubmissionId != null && providerSubmissionStatus != null) {
            return;
        }

        if (familySubmissionId != null) {
            Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId);
            if (familySubmissionOptional.isPresent()) {
                Submission familySubmission = familySubmissionOptional.get();

                // Used to display the correct provider name, if available, for the first provider screen
                httpSession.setAttribute(SESSION_KEY_SELECTED_PROVIDER_NAME, getProviderName(familySubmission));

                // To be used on subsequent screens to validate provider inputs == these values
                httpSession.setAttribute(SESSION_KEY_FAMILY_CONFIRMATION_CODE, familySubmission.getShortCode());
                httpSession.setAttribute(SESSION_KEY_FAMILY_SUBMISSION_ID, familySubmission.getId());

                // In Prod, there should always be a submittedAt date, but for Staging it's possible to skip around in the flow and never submit
                LocalDate submittedAtDate =
                        familySubmission.getSubmittedAt() != null ? familySubmission.getSubmittedAt().toLocalDate()
                                : null;
                if (submittedAtDate == null) {
                    log.warn("No submittedAt date found for submission " + submission.getId());
                }
                ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");
                ZonedDateTime todaysDate = OffsetDateTime.now().atZoneSameInstant(chicagoTimeZone);
                if (providerApplicationHasExpired(familySubmission, todaysDate)) {
                    httpSession.setAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS, ProviderSubmissionStatus.EXPIRED.name());
                } else {
                    boolean hasResponse = false;
                    if (familySubmission.getInputData().get("providerResponseSubmissionId") != null) {
                        // The above value should be set on the family submission whenever a provider first submits
                        // their response.
                        Optional<Submission> providerResponseSubmission = submissionRepositoryService.findById(UUID.fromString(
                                familySubmission.getInputData().get("providerResponseSubmissionId").toString()));
                        if (providerResponseSubmission.isPresent() && providerResponseSubmission.get().getSubmittedAt() != null) {
                            // Again, the UUID should only have been set after a successful provider submission, but double
                            // checking that it's actually been submitted isn't a bad idea
                            hasResponse = true;
                        }
                    }
                    if (hasResponse) {
                        httpSession.setAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS, ProviderSubmissionStatus.RESPONDED.name());
                    } else {
                        httpSession.setAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS, ProviderSubmissionStatus.ACTIVE.name());
                    }
                }
            }
        } else {
            // If we don't have a family submission, we use the Active status but without any
            // data pre-loaded.
            httpSession.setAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS, ProviderSubmissionStatus.ACTIVE.name());

            Locale locale = LocaleContextHolder.getLocale();
            String placeholderProviderName = messageSource.getMessage("provider-response-submit-start.provider-placeholder", null,
                    locale);
            httpSession.setAttribute(SESSION_KEY_SELECTED_PROVIDER_NAME, placeholderProviderName);
        }
    }

    private String getProviderName(Submission familySubmission) {
        Map<String, Object> inputData = familySubmission.getInputData();
        return (String) inputData.get("familyIntendedProviderName");
    }
}
