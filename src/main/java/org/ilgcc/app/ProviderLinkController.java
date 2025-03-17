package org.ilgcc.app;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_CAME_FROM_HOME_PAGE;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_CONFIRMATION_CODE;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_FAMILY_SUBMISSION_ID;
import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_SUBMISSION_MAP;

@Slf4j
@Controller
public class ProviderLinkController {

    private final SubmissionRepositoryService submissionRepositoryService;

    public ProviderLinkController(SubmissionRepositoryService submissionRepositoryService) {
        this.submissionRepositoryService = submissionRepositoryService;
    }

    /**
     * @param session
     * @param request
     * @param confirmationCode The confirmation code used to look up the submission
     * @return
     */
    @GetMapping(value = {"s", "s/{confirmationCode}", "providerresponse/submit/{confirmationCode}"})
    String loadFamilySubmission(HttpSession session, HttpServletRequest request,
            @PathVariable(required = false) String confirmationCode,
            @RequestHeader(value = "Referer", required = false) String referer
    ) throws URISyntaxException {
        session.invalidate();

        // create a new session and populate it with an empty FFL submission map
        HttpSession newSession = request.getSession();
        newSession.setAttribute(SESSION_KEY_SUBMISSION_MAP, new HashMap<String, UUID>());

        String sanitizedConfirmationCode =
                (confirmationCode != null) ? confirmationCode.replace('\n', '_').replace('\r', '_') : null;

        log.info("Loading submission for code " + sanitizedConfirmationCode);

        if (sanitizedConfirmationCode != null) {
            Optional<Submission> familySubmission = submissionRepositoryService.findByShortCode(
                    sanitizedConfirmationCode.toUpperCase());
            if (familySubmission.isPresent()) {
                setSessionUrl(familySubmission.get(), sanitizedConfirmationCode);
                setFamilySessionData(familySubmission.get(), newSession);
                checkRefererValue(referer, newSession);
            } else {
                log.error("Unable to load submission for code " + sanitizedConfirmationCode);
                return "redirect:/error-invalid-code";
            }
        }

        return "redirect:/flow/providerresponse/submit-start";
    }

    private void setSessionUrl(Submission familySubmission, String sanitizedConfirmationCode) {
        Map<String, String> urlParams = familySubmission.getUrlParams();
        if (urlParams == null) {
            urlParams = new HashMap<>();
        }

        urlParams.put("conf_code", sanitizedConfirmationCode);
        familySubmission.setUrlParams(urlParams);
        submissionRepositoryService.save(familySubmission);
    }

    private void setFamilySessionData(Submission familySubmission, HttpSession currentSession) {
        currentSession.setAttribute(SESSION_KEY_FAMILY_SUBMISSION_ID, familySubmission.getId());
        currentSession.setAttribute(SESSION_KEY_FAMILY_CONFIRMATION_CODE, familySubmission.getShortCode());
    }

    private static void checkRefererValue(String referer, HttpSession currentSession) throws URISyntaxException {
        if (referer != null) {
            URI refererUri = new URI(referer);
            if (("/").equals(refererUri.getPath())) {
                currentSession.setAttribute(SESSION_KEY_CAME_FROM_HOME_PAGE, true);
            }
        }
    }
}
