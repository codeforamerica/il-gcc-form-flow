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
   * Renders the website index page.
   *
   * @param httpSession The current HTTP session, not null.
   * @return the static page template.
   */
  @GetMapping("provider-responses/submit-start")
  String getScreen(HttpSession httpSession, @RequestParam(required = false) String status) {
    httpSession.invalidate(); // For dev, reset session if you visit home

    return "provider-responses/submit-start";
  }
}
