package org.ilgcc.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller responsible for handling development environment specific requests. It is active only when the application is
 * running in the `dev` profile.
 */
@Controller
@EnableAutoConfiguration
@Slf4j
@Profile("dev")
@RequestMapping("gcc-dev")
public class GccDevController {

  /**
   * Handles the GET request to `dev/icons`. This method renders a page that displays the current icons available in the
   * development environment.
   *
   * @return The name of the HTML page that shows the icons.
   */
  @GetMapping("/icons")
  String getIcons() {
    return "fragments/gcc-icons";
  }
}
