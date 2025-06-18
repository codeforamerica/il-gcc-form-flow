package org.ilgcc.app.submission.actions;


import static org.ilgcc.app.utils.ProviderSubmissionUtilities.calculateProviderApplicationResponseStatus;
import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getProviderApplicationResponseStatus;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_SUBMISSION_ID;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_PROVIDER_SUBMISSION_STATUS;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_SELECTED_PROVIDER_NAME;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.springframework.beans.factory.annotation.Value;
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
    private boolean enableMultipleProviders;

    public SetProviderSubmissionStatusAndConnectApplications(HttpSession httpSession, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService, @Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.httpSession = httpSession;
        this.messageSource = messageSource;
        this.submissionRepositoryService = submissionRepositoryService;
        this.enableMultipleProviders = enableMultipleProviders;
    }

    @Override
    public void run(Submission providerSubmission) {
        // Set the data from the session if it exists
        familySubmissionId = (UUID) httpSession.getAttribute(SESSION_KEY_FAMILY_SUBMISSION_ID);

        if (familySubmissionId != null) {
            Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId);
            if (familySubmissionOptional.isPresent()) {
                Submission familySubmission = familySubmissionOptional.get();
                connectProviderSubmissionToFamilySubmission(providerSubmission);
                Optional<SubmissionStatus> statusFromFamilyApp = getProviderApplicationResponseStatus(familySubmission);
                if (statusFromFamilyApp.isPresent()) {
                    httpSession.setAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS,
                            statusFromFamilyApp.get());
                } else {
                    // TODO: Is this still needed or at this point should all family submissions have the statusFromFamilyApp above?
                    SubmissionStatus calculatedSubmissionStatus = calculateProviderApplicationResponseStatus(familySubmission);
                    httpSession.setAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS,
                            calculatedSubmissionStatus);
                    familySubmission.getInputData()
                            .put("providerApplicationResponseStatus", calculatedSubmissionStatus);
                    submissionRepositoryService.save(familySubmission);
                }
                httpSession.setAttribute(SESSION_KEY_SELECTED_PROVIDER_NAME, getProviderName(familySubmission));
            }
        } else {
            // Set Status as active and don't set any data when there is no submissionId
            httpSession.setAttribute(SESSION_KEY_PROVIDER_SUBMISSION_STATUS, SubmissionStatus.ACTIVE.name());

            Locale locale = LocaleContextHolder.getLocale();
            httpSession.setAttribute(SESSION_KEY_SELECTED_PROVIDER_NAME,
                    messageSource.getMessage("provider-response-submit-start.provider-placeholder", null, locale));
        }
    }

    private void connectProviderSubmissionToFamilySubmission(Submission providerSubmission) {
        providerSubmission.getInputData().put("familySubmissionId", familySubmissionId.toString());
        submissionRepositoryService.save(providerSubmission);
    }

    private String getProviderName(Submission familySubmission) {
        Map<String, Object> inputData = familySubmission.getInputData();

        if (enableMultipleProviders) {
            List<Map<String, Object>> providers = SubmissionUtilities.getProviders(familySubmission);
            if (providers.size() == 1) {
                // If we have exactly 1 provider, just get that provider's name and use it.
                return providers.getFirst().get("familyIntendedProviderName").toString();
            } else {
                // If we don't have exactly 1 provider, we don't know which provider is using the confirmation code yet, so we
                // can just default to the placeholder.
                Locale locale = LocaleContextHolder.getLocale();
                return messageSource.getMessage("provider-response-submit-start.provider-placeholder", null, locale);
            }
        } else {
            return (String) inputData.get("familyIntendedProviderName");
        }

    }

}
