package org.ilgcc.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SitemapController {
    
    @Value("${il-gcc.base-url}")
    private String baseUrl;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String getSitemap() {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
              <url>
                <loc>%s/</loc>
                <changefreq>monthly</changefreq>
                <priority>1.0</priority>
              </url>
              <url>
                <loc>%s/faq</loc>
                <changefreq>monthly</changefreq>
                <priority>0.8</priority>
              </url>
              <url>
                <loc>%s/privacy-policy</loc>
                <changefreq>monthly</changefreq>
                <priority>0.5</priority>
              </url>
            </urlset>
            """.formatted(baseUrl, baseUrl, baseUrl);
    }
}
