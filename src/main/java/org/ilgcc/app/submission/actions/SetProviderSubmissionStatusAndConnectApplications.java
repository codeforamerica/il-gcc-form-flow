package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_SUBMISSION_ID;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_PROVIDER_SUBMISSION_STATUS;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_SELECTED_PROVIDER_NAME;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetProviderSubmissionStatusAndConnectApplications implements Action {

    private final HttpSession httpSession;
    private final MessageSource messageSource;
    private final SubmissionRepositoryService submissionRepositoryService;
    private UUID familySubmissionId;
    private String providerSubmissionStatus;

    private final String PROVIDER_RESPONSE_STATUS_INPUT_NAME = "providerApplicationStatus";

    public SetProviderSubmissionStatusAndConnectApplications(HttpSession httpSession, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        this.httpSession = httpSession;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission providerSubmission) {
        // Set the data from the session if it exists
        setIdAndStatusFromSession();

        if (familySubmissionId != null && providerSubmissionStatus != null) {
            connectProviderSubmissionToFamilySubmission(providerSubmission);
            return;
        }

        if (familySubmissionId != null) {
            Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId);
            if (familySubmissionOptional.isPresent()) {
                connectProviderSubmissionToFamilySubmission(providerSubmission);
                httpSession.setAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS, ProviderSubmissionUtilities.setProviderSubmissionStatus(
                        familySubmissionOptional.get()));
            }
        } else {
            // Set Status as active and don't set any data when there is no submissionId
            httpSession.setAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS, SubmissionStatus.ACTIVE);

            Locale locale = LocaleContextHolder.getLocale();
            String placeholderProviderName = messageSource.getMessage("provider-response-submit-start.provider-placeholder", null,
                    locale);
            httpSession.setAttribute(SESSION_KEY_SELECTED_PROVIDER_NAME, placeholderProviderName);
        }
    }

    private void setIdAndStatusFromSession() {
        //toDO: what happens if the attribute does not exist?
        familySubmissionId = (UUID) httpSession.getAttribute(SESSION_KEY_FAMILY_SUBMISSION_ID);
        providerSubmissionStatus = (String) httpSession.getAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS);
    }

    private void connectProviderSubmissionToFamilySubmission(Submission providerSubmission) {
        providerSubmission.getInputData().put("familySubmissionId", familySubmissionId);
        submissionRepositoryService.save(providerSubmission);
    }


}
