package org.ilgcc.app.utils;

import formflow.library.utils.RegexUtils;
import jakarta.servlet.http.HttpSession;
import org.ilgcc.app.email.sendgrid.SendGridEmailValidationService;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SendGridUtilities {

    public static void callSendGridAndValidateEmail(Locale locale, Map<String, List<String>> errorMessages,
                                                                  String email, SendGridEmailValidationService sendGridEmailValidationService, String inputName,
                                                                  MessageSource messageSource, HttpSession httpSession,String sessionKey, Boolean isFamilyEmail) {
        if (email.matches(RegexUtils.EMAIL_REGEX)) {
            try {
                HashMap<String, String> emailValidationResult = sendGridEmailValidationService.validateEmail(email, isFamilyEmail);
                if (emailValidationResult.getOrDefault("endpointReached", "").equals("success")) {
                    if (!emailValidationResult.get("emailIsValid").equals("true")) {
                        if (httpSession.getAttribute(sessionKey) != null && httpSession.getAttribute(sessionKey).equals(email)) {
                            return;
                        }
                        if (emailValidationResult.get("hasSuggestion").equals("true")) {
                            errorMessages.put(inputName, List.of(messageSource.getMessage("errors.invalid-email.with-suggested-email-address",
                                    new Object[]{emailValidationResult.get("suggestedEmail")}, locale)));

                        } else {
                            errorMessages.put(inputName,
                                    List.of(messageSource.getMessage("errors.invalid-email.no-suggested-email-address", null, locale)));
                        }
                        httpSession.setAttribute(sessionKey, email);
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
