package org.ilgcc.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RobotsController {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    @Value("${il-gcc.base-url}")
    private String baseUrl;

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getRobotsTxt() {
        if ("production".equalsIgnoreCase(activeProfile)) {
            return String.format("""
                User-agent: *
                Disallow: /
                Allow: /$
                Allow: /faq$
                Allow: /privacy$
                Sitemap: %s/sitemap.xml
                """, baseUrl);
        } else {
            return """
                User-agent: *
                Disallow: /
                """;
        }
    }
}
