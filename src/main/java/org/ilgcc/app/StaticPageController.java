package org.ilgcc.app;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * A controller to render static pages that are not in any flow.
 */
@Controller
public class StaticPageController {
    /**
     * Renders the website index page.
     *
     * @param httpSession The current HTTP session, not null.
     * @return the static page template.
     */
    @GetMapping("/")
    ModelAndView getIndex(HttpSession httpSession) {
        httpSession.invalidate(); // For dev, reset session if you visit home

        HashMap<String, Object> model = new HashMap<>();
        return new ModelAndView("index", model);
    }

    /**
     * Renders the website faq page.
     *
     * @return the static page template
     */
    @GetMapping("/faq")
    String getFaq() {
        return "faq";
    }

    /**
     * Renders the website privacy page.
     *
     * @return the static page template
     */
    @GetMapping("/privacy")
    String getPrivacy() {
        return "privacy";
    }

    /**
     * Renders the page to redirect to when a flow is disabled.
     *
     * @return the static page template
     */
    @GetMapping("/disabledFeature")
    String getDisabled() {
        return "disabledFeature";
    }

    /**
     * Renders the error invalid code screen
     *
     * @return the static page template
     */
    @GetMapping("/error-invalid-code")
    String getInvalidCode() {
        return "error-invalid-code";
    }
}
