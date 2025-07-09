package org.ilgcc.app.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.ilgcc.app.StaticPageController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = { formflow.library.ScreenController.class, StaticPageController.class })
public class ScreenControllerAdvice {

    private final CanonicalUrlService canonicalUrlService;

    public ScreenControllerAdvice(CanonicalUrlService canonicalUrlService) {
        this.canonicalUrlService = canonicalUrlService;
    }
    
    @ModelAttribute("canonicalUrl")
    public String addCanonicalUrl(HttpServletRequest request) {
        return canonicalUrlService.getCanonicalUrl(request);
    }
}
