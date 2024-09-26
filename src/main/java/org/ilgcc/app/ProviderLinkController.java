package org.ilgcc.app;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class ProviderLinkController {

    private final static String SESSION_KEY_CLIENT_SUBMISSION_ID = "clientSubmissionId";
    private final static String SESSION_KEY_SUBMISSION_MAP = "submissionMap";

    private final SubmissionRepositoryService submissionRepositoryService;

    public ProviderLinkController(SubmissionRepositoryService submissionRepositoryService) {
        this.submissionRepositoryService = submissionRepositoryService;
    }

    /**
     * @param session
     * @param request
     * @param confirmationCode The confirmation code used to look up the submission
     * @param utmMedium        The utm_medium param, likely email vs message vs blank
     * @return
     */
    @GetMapping("providerresponse/submit")
    String loadClientSubmission(HttpSession session, HttpServletRequest request,
            @RequestParam(name = "conf_code", required = false) String confirmationCode,
            @RequestParam(name = "utm_medium", required = false) String utmMedium) {

        session.invalidate();

        // create a new session and populate it with an empty FFL submission map
        HttpSession newSession = request.getSession();
        newSession.setAttribute(SESSION_KEY_SUBMISSION_MAP, new HashMap<String, UUID>());

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

        return "redirect:/flow/providerresponse/submit-start";
    }
}
