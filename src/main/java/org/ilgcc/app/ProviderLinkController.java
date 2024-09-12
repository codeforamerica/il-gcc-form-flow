package org.ilgcc.app;

import formflow.library.config.FormFlowConfigurationProperties;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A controller to render static pages that are not in any flow.
 */
@Controller
public class ProviderLinkController {

    FormFlowConfigurationProperties formFlowConfigurationProperties;

    public ProviderLinkController(FormFlowConfigurationProperties formFlowConfigurationProperties) {
        this.formFlowConfigurationProperties = formFlowConfigurationProperties;
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
}
