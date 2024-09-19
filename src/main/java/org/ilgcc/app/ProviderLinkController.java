package org.ilgcc.app;

import formflow.library.config.FormFlowConfigurationProperties;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * A controller to render static pages that are not in any flow.
 */
@Slf4j
@Controller
public class ProviderLinkController {

    FormFlowConfigurationProperties formFlowConfigurationProperties;

    SubmissionRepositoryService submissionRepositoryService;

    public ProviderLinkController(FormFlowConfigurationProperties formFlowConfigurationProperties, SubmissionRepositoryService submissionRepositoryService) {
        this.formFlowConfigurationProperties = formFlowConfigurationProperties;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    /**
     * Renders the provider-responses website initial page.
     *
     * @param httpSession The current HTTP session, not null.
     * @param status      Placeholder for application ID which will be used to determine provider response status
     * @return the static page template.
     */
    @GetMapping("provider-responses/submit-start")
    String getScreen(HttpSession httpSession, @RequestParam(required = false) String status) {
        httpSession.invalidate(); // For dev, reset session if you visit home

        return "provider-responses/submit-start";
    }

    /**
     * @param confirmationCode The confirmation code used to look up the submission
     * @param utmMedium        The utm_medium param, likely email vs message vs blank
     * @return
     */
    @GetMapping("provider-responses/submit")
    String loadSubmission(RedirectAttributes redirectAttributes,
            @RequestParam(name = "conf_code") String confirmationCode,
            @RequestParam(name = "utm_medium", required = false) String utmMedium) throws IOException {

        String sanitizedConfirmationCode = confirmationCode.replace('\n', '_').replace('\r', '_');
        String sanitizedUtmMedium = (utmMedium != null) ? utmMedium.replace('\n', '_').replace('\r', '_') : "unknown";
        log.info("Loading submission for code " + sanitizedConfirmationCode + " from medium " + sanitizedUtmMedium);

        Optional<Submission> submission = submissionRepositoryService.findByShortCode(sanitizedConfirmationCode);
        if (submission.isPresent()) {
            Submission s = submission.get();
            Map<String, String> urlParams = s.getUrlParams();
            if (urlParams == null) {
                urlParams = new HashMap<String, String>();
            }

            // TODO: Is this necessary?
            urlParams.put("utm_medium", sanitizedUtmMedium);
            urlParams.put("conf_code", sanitizedConfirmationCode);
            s.setUrlParams(urlParams);
            submissionRepositoryService.save(s);
        } else {
            log.info("Unable to load submission for code " + sanitizedConfirmationCode);
            // TODO: Handle invalid confirmation code
        }

        redirectAttributes.addAttribute("status", "ACTIVE");
//        response.sendRedirect("submit-start");
        return "redirect:submit-start";
    }
}
