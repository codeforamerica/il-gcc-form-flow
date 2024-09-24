package org.ilgcc.app;


import static java.time.temporal.ChronoUnit.DAYS;

import formflow.library.config.FormFlowConfigurationProperties;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ChildCareProvider;
import org.ilgcc.app.utils.enums.ProviderSubmissionStatus;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class ProviderLinkController {

    private final static String SESSION_KEY_SELECTED_PROVIDER_NAME = "selectedProviderName";
    private final static String SESSION_KEY_SELECTED_PROVIDER_ID = "selectedProviderId";
    private final static String SESSION_KEY_CLIENT_SUBMISSION_STATUS = "clientSubmissionStatus";
    private final static String SESSION_KEY_CLIENT_SUBMISSION_ID = "clientSubmissionId";

    private final FormFlowConfigurationProperties formFlowConfigurationProperties;

    private final SubmissionRepositoryService submissionRepositoryService;

    private final MessageSource messageSource;

    public ProviderLinkController(FormFlowConfigurationProperties formFlowConfigurationProperties,
            SubmissionRepositoryService submissionRepositoryService, MessageSource messageSource) {
        this.formFlowConfigurationProperties = formFlowConfigurationProperties;
        this.submissionRepositoryService = submissionRepositoryService;
        this.messageSource = messageSource;
    }

    /**
     * Renders the provider-responses website initial page.
     *
     * @param session The current HTTP session, not null.
     * @return the static page template.
     */
    @GetMapping("provider-responses/submit-start")
    String getScreen(HttpSession session) {
        UUID clientSubmissionId = (UUID) session.getAttribute(SESSION_KEY_CLIENT_SUBMISSION_ID);
        if (clientSubmissionId != null) {
            Optional<Submission> clientSubmission = submissionRepositoryService.findById(clientSubmissionId);
            if (clientSubmission.isPresent()) {
                Submission clientSubmissionInfo = clientSubmission.get();
                ChildCareProvider provider = ChildCareProvider.valueOf(
                        clientSubmissionInfo.getInputData().get("dayCareChoice").toString());

                session.setAttribute(SESSION_KEY_SELECTED_PROVIDER_NAME, provider.getDisplayName());
                session.setAttribute(SESSION_KEY_SELECTED_PROVIDER_ID, provider.getIdNumber());
                session.setAttribute("confirmationCode", clientSubmissionInfo.getShortCode());

                LocalDate submittedAtDate = clientSubmissionInfo.getSubmittedAt().toLocalDate();
                LocalDate todaysDate = LocalDate.now();
                if (DAYS.between(submittedAtDate, todaysDate) >= 4) {
                    session.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.EXPIRED);
                } else {
                    boolean hasResponse = false;
                    // TODO: Lookup and see if the client submission has a provider response as well
                    if (hasResponse) {
                        session.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.RESPONDED);
                    } else {
                        session.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.ACTIVE);
                    }
                }
            }
        } else {
            // If we don't have a client submission, we use the Active status but without any
            // data pre-loaded.
            session.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_STATUS, ProviderSubmissionStatus.ACTIVE);

            Locale locale = LocaleContextHolder.getLocale();
            String placeholderProviderName = messageSource.getMessage("provider-response.submit-start.provider-placeholder", null,
                    locale);
            session.setAttribute(SESSION_KEY_SELECTED_PROVIDER_NAME, placeholderProviderName);
        }

        return "provider-responses/submit-start";
    }


    /**
     * @param session
     * @param request
     * @param confirmationCode The confirmation code used to look up the submission
     * @param utmMedium        The utm_medium param, likely email vs message vs blank
     * @return
     */
    @GetMapping("provider-responses/submit")
    String loadSubmission(HttpSession session, HttpServletRequest request,
            @RequestParam(name = "conf_code", required = false) String confirmationCode,
            @RequestParam(name = "utm_medium", required = false) String utmMedium) {

        session.invalidate();
        HttpSession newSession = request.getSession(); // create session

        String sanitizedConfirmationCode =
                (confirmationCode != null) ? confirmationCode.replace('\n', '_').replace('\r', '_') : null;
        String sanitizedUtmMedium = (utmMedium != null) ? utmMedium.replace('\n', '_').replace('\r', '_') : null;
        log.info("Loading submission for code " + sanitizedConfirmationCode + " from medium " + sanitizedUtmMedium);

        if (sanitizedConfirmationCode != null) {
            Optional<Submission> submission = submissionRepositoryService.findByShortCode(sanitizedConfirmationCode);
            if (submission.isPresent()) {
                Submission s = submission.get();
                Map<String, String> urlParams = s.getUrlParams();
                if (urlParams == null) {
                    urlParams = new HashMap<String, String>();
                }

                // TODO: Is this necessary? We probably want to save/set these on the *new* Submission for the provider
                urlParams.put("utm_medium", sanitizedUtmMedium);
                urlParams.put("conf_code", sanitizedConfirmationCode);
                s.setUrlParams(urlParams);
                submissionRepositoryService.save(s);

                newSession.setAttribute(SESSION_KEY_CLIENT_SUBMISSION_ID, s.getId());
            } else {
                log.error("Unable to load submission for code " + sanitizedConfirmationCode);
                return "redirect:/error";
            }
        }

        return "redirect:submit-start";
    }
}
