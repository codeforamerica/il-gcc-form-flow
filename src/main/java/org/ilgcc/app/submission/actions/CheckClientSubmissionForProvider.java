package org.ilgcc.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ChildCareProvider;
import static org.ilgcc.app.utils.ProviderSubmissionUtilities.providerApplicationHasExpired;
import org.ilgcc.app.utils.enums.ProviderSubmissionStatus;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckClientSubmissionForProvider implements Action {

    private final HttpSession httpSession;
    private final MessageSource messageSource;
    private final SubmissionRepositoryService submissionRepositoryService;

    private final static String SESSION_KEY_SELECTED_PROVIDER_NAME = "selectedProviderName";
    private final static String SESSION_KEY_SELECTED_PROVIDER_ID = "selectedProviderId";
    private final static String SESSION_KEY_CLIENT_SUBMISSION_STATUS = "clientSubmissionStatus";
    private final static String SESSION_KEY_CLIENT_SUBMISSION_ID = "clientSubmissionId";
    private final static String SESSION_KEY_CLIENT_CONFIRMATION_CODE = "confirmationCode";

    public CheckClientSubmissionForProvider(HttpSession httpSession, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        this.httpSession = httpSession;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission submission) {
        UUID clientSubmissionId = (UUID) httpSession.getAttribute(SESSION_KEY_CLIENT_SUBMISSION_ID);
        if (clientSubmissionId != null) {
            Optional<Submission> clientSubmission = submissionRepositoryService.findById(clientSubmissionId);
            if (clientSubmission.isPresent()) {
                Submission clientSubmissionInfo = clientSubmission.get();
                ChildCareProvider provider = ChildCareProvider.valueOf(
                        clientSubmissionInfo.getInputData().get("dayCareChoice").toString());

                // Used to display the correct provider name, if available, for the first provider screen
                httpSession.setAttribute(SESSION_KEY_SELECTED_PROVIDER_NAME, provider.getDisplayName());

                // To be used on subsequent screens to validate provider inputs == these values
                httpSession.setAttribute(SESSION_KEY_SELECTED_PROVIDER_ID, provider.getIdNumber());
                httpSession.setAttribute(SESSION_KEY_CLIENT_CONFIRMATION_CODE, clientSubmissionInfo.getShortCode());

                // In Prod, there should always be a submittedAt date, but for Staging it's possible to skip around in the flow and never submit
                LocalDate submittedAtDate = clientSubmissionInfo.getSubmittedAt() != null ? clientSubmissionInfo.getSubmittedAt().toLocalDate() : null;
                if (submittedAtDate == null) {
                    log.warn("No submittedAt date found for submission " + submission.getId());
                }
                ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");
                ZonedDateTime todaysDate = OffsetDateTime.now().atZoneSameInstant(chicagoTimeZone);
                if (providerApplicationHasExpired(clientSubmissionInfo, todaysDate)) {
                    httpSession.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.EXPIRED.name());
                } else {
                      boolean hasResponse = false;
                    if (clientSubmissionInfo.getInputData().get("providerResponseSubmissionId") != null) {
                        // The above value should be set on the client submission whenever a provider first submits
                        // their response.
                        Optional<Submission> providerResponseSubmission = submissionRepositoryService.findById(UUID.fromString(
                                clientSubmissionInfo.getInputData().get("providerResponseSubmissionId").toString()));
                        if (providerResponseSubmission.isPresent() && providerResponseSubmission.get().getSubmittedAt() != null) {
                            // Again, the UUID should only have been set after a successful provider submission, but double
                            // checking that it's actually been submitted isn't a bad idea
                            hasResponse = true;
                        }
                    }
                    if (hasResponse) {
                        httpSession.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.RESPONDED.name());
                    } else {
                        httpSession.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.ACTIVE.name());
                    }
                }
            }
        } else {
            // If we don't have a client submission, we use the Active status but without any
            // data pre-loaded.
            httpSession.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.ACTIVE);

            Locale locale = LocaleContextHolder.getLocale();
            String placeholderProviderName = messageSource.getMessage("provider-response-submit-start.provider-placeholder", null,
                    locale);
            httpSession.setAttribute(SESSION_KEY_SELECTED_PROVIDER_NAME, placeholderProviderName);
        }
    }
}
