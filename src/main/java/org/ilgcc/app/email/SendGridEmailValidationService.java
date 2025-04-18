package org.ilgcc.app.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import formflow.library.utils.RegexUtils;
import java.io.IOException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendGridEmailValidationService {

    private boolean ENABLE_EMAIL_VALIDATION;
    private final SendGrid sendGrid;

    @Autowired
    public SendGridEmailValidationService(
            @Value("${sendgrid.enable-sendgrid-email-validation:false}") boolean enableSendgridEmailValidation,
            @Value("${sendgrid.email-validation-api-key}") String apiKey) {
        ENABLE_EMAIL_VALIDATION = enableSendgridEmailValidation;
        this.sendGrid = new SendGrid(apiKey);
    }

    // Constructor for mocking SendGrid service for tests
    public SendGridEmailValidationService(boolean enableValidation, SendGrid sendGrid) {
        this.ENABLE_EMAIL_VALIDATION = enableValidation;
        this.sendGrid = sendGrid;
    }

    public HashMap<String, String> validateEmail(String emailAddress) throws IOException {
        HashMap<String, String> emailValidationResult = new HashMap<>();
        if (emailAddress == null || emailAddress.isBlank() || !emailAddress.matches(RegexUtils.EMAIL_REGEX)) {
            log.info(
                    "Email address will not be validated because email address is null, empty, or fails our basic email validation");
            return emailValidationResult;
        }
        if (ENABLE_EMAIL_VALIDATION) {
            try {
                Response response = getSendGridResponse(emailAddress);
                if (sendGridFailedToProcessEmailValidationRequest(response)) {
                    emailValidationResult.put("endpointReached", "failed");
                    return emailValidationResult;
                }
                ObjectMapper mapper = new ObjectMapper();
                SendGridValidationResponseBody responseBody = mapper.readValue(response.getBody(),
                        SendGridValidationResponseBody.class);
                emailValidationResult.put("endpointReached", "success");

                Boolean emailIsValid = isValidEmail(responseBody);
                emailValidationResult.put("emailIsValid", emailIsValid.toString());
                if (!emailIsValid) {
                    Boolean hasSuggestedEmail = responseBody.getResult().hasSuggestedEmailAddress();
                    emailValidationResult.put("hasSuggestion", hasSuggestedEmail.toString());
                    if (hasSuggestedEmail) {
                        emailValidationResult.put("suggestedEmail", responseBody.getResult().getSuggestedEmailAddress());
                    }
                }
            } catch (Exception e) {
                log.error("Sendgrid request failed. Error: {}", e.getMessage());
            }
        }
        return emailValidationResult;
    }

    protected Response getSendGridResponse(String emailAddress) throws IOException {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("validations/email");
        request.setBody(new JSONObject(new HashMap<String, Object>() {
            {
                put("email", emailAddress);
            }
        }).toString());
        return sendGrid.api(request);
    }

    public Boolean sendGridFailedToProcessEmailValidationRequest(Response response) {
        boolean sendGridRequestFailed = response.getStatusCode() != 200;
        if (sendGridRequestFailed) {
            log.error("Sendgrid request failed.  Error code: {}", response.getStatusCode());
        }
        return sendGridRequestFailed;
    }

    public Boolean isValidEmail(@NotNull SendGridValidationResponseBody responseBody) {
        SendGridValidationResponseBody.Result result = responseBody.getResult();
        return result.hasValidAddressSyntax() &&
                result.hasMxOrARecord() &&
                !result.isSuspectedDisposableAddress() &&
                !result.hasKnownBounces() &&
                !result.hasSuspectedBounces();
    }
}
