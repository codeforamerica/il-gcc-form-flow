package org.ilgcc.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class RobotsControllerUnitTest {

    @Test
    void productionProfile_returnsFullRobotsWithSitemap() {
        RobotsController controller = new RobotsController();
        ReflectionTestUtils.setField(controller, "activeProfile", "production");
        ReflectionTestUtils.setField(controller, "baseUrl", "https://www.getchildcareil.org");

        String body = controller.getRobotsTxt();

        assertThat(body.lines().toList()).containsExactly(
                "User-agent: *",
                "Disallow: /",
                "Allow: /$",
                "Allow: /faq$",
                "Allow: /privacy$",
                "Sitemap: https://www.getchildcareil.org/sitemap.xml"
        );
    }

    @Test
    void nonProductionProfile_returnsDisallowAll() {
        RobotsController controller = new RobotsController();
        ReflectionTestUtils.setField(controller, "activeProfile", "qa");
        ReflectionTestUtils.setField(controller, "baseUrl", "https://ignored.example");

        String body = controller.getRobotsTxt();

        assertThat(body.lines().toList()).containsExactly(
                "User-agent: *",
                "Disallow: /"
        );
    }
}