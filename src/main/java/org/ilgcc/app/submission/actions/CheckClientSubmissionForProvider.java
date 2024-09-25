package org.ilgcc.app.submission.actions;

import static java.time.temporal.ChronoUnit.DAYS;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ChildCareProvider;
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

                httpSession.setAttribute(SESSION_KEY_SELECTED_PROVIDER_NAME, provider.getDisplayName());
                httpSession.setAttribute(SESSION_KEY_SELECTED_PROVIDER_ID, provider.getIdNumber());
                httpSession.setAttribute("confirmationCode", clientSubmissionInfo.getShortCode());

                LocalDate submittedAtDate = clientSubmissionInfo.getSubmittedAt().toLocalDate();
                LocalDate todaysDate = LocalDate.now();
                if (DAYS.between(submittedAtDate, todaysDate) >= 4) {
                    httpSession.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.EXPIRED);
                } else {
                    boolean hasResponse = false;
                    // TODO: Lookup and see if the client submission has a provider response as well
                    if (hasResponse) {
                        httpSession.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.RESPONDED);
                    } else {
                        httpSession.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.ACTIVE);
                    }
                }
            }
        } else {
            // If we don't have a client submission, we use the Active status but without any
            // data pre-loaded.
            httpSession.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.ACTIVE);

            Locale locale = LocaleContextHolder.getLocale();
            String placeholderProviderName = messageSource.getMessage("provider-response.submit-start.provider-placeholder", null,
                    locale);
            httpSession.setAttribute(SESSION_KEY_SELECTED_PROVIDER_NAME, placeholderProviderName);
        }
    }
}
