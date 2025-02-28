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
            Optional<Submission> submission = submissionRepositoryService.findByShortCode(sanitizedConfirmationCode.toUpperCase());
            if (submission.isPresent()) {
                Submission s = submission.get();
                Map<String, String> urlParams = s.getUrlParams();
                if (urlParams == null) {
                    urlParams = new HashMap<>();
                }

                urlParams.put("conf_code", sanitizedConfirmationCode);
                s.setUrlParams(urlParams);
                submissionRepositoryService.save(s);

                newSession.setAttribute(SESSION_KEY_FAMILY_SUBMISSION_ID, s.getId());

                if (referer != null) {
                    URI refererUri = new URI(referer);
                    if (("/").equals(refererUri.getPath())) {
                        newSession.setAttribute(SESSION_KEY_CAME_FROM_HOME_PAGE, true);
                    }
                }
            } else {
                log.error("Unable to load submission for code " + sanitizedConfirmationCode);
                return "redirect:/error-invalid-code";
            }
        }

        return "redirect:/flow/providerresponse/submit-start";
    }
}
