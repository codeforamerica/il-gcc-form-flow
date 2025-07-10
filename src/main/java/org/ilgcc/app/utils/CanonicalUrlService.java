package org.ilgcc.app.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CanonicalUrlService {
    
    @Value("${il-gcc.base-url}")
    private String baseUrl;

    public String getCanonicalUrl(HttpServletRequest request) {
        String path = request.getRequestURI();

        if (path.equals("/") || path.equals("/faq") || path.equals("/privacy")) {
            return baseUrl + path;
        }

        if (path.startsWith("/flow/gcc/") || path.startsWith("/flow/providerresponse/")) {
            return baseUrl + "/";
        }

        return null;
    }
}
