package org.ilgcc.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public RedirectView handleIllegalArgumentException(IllegalArgumentException e) {
        log.info(String.format("IllegalArgumentException handled: %s", e.getMessage()));
        return new RedirectView("/error");
    }
}
