package org.ilgcc.app.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import java.io.IOException;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class SendGridEmailValidationService {
    /*
        This service will send a request to the SendGrid email validation api
     */
  private final boolean ENABLE_EMAIL_VALIDATION;
  private final SendGrid sendGrid = new SendGrid(System.getenv("SENDGRID_EMAIL_VALIDATION_API_KEY"));

  public SendGridEmailValidationService(
        @Value("${sendgrid.enable-sendgrid-email-validation:false}") boolean enableSendgridEmailValidation) {
        ENABLE_EMAIL_VALIDATION = enableSendgridEmailValidation;
  }

  public HashMap<String, String> validateEmail(String emailAddress) throws IOException {
        HashMap<String, String> emailValidationResult = new HashMap<>();
        Response response = getSendGridResponse(emailAddress);
        if(!flagIsOnAndApiIsWorking(response)) {
          emailValidationResult.put("endpointReached", "failed");
          return emailValidationResult;
        }
        ObjectMapper mapper = new ObjectMapper();
        SendGridValidationResponseBody responseBody = mapper.readValue(response.getBody(), SendGridValidationResponseBody.class);
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
        return emailValidationResult;
    }

    private Response getSendGridResponse (String emailAddress) throws IOException {
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

    public Boolean flagIsOnAndApiIsWorking(Response response) throws IOException {
      if  (ENABLE_EMAIL_VALIDATION) {
          return response.getStatusCode() == 200;
      }  else {
          log.debug("Sendgrid Flag is off");
          return false;
      }
    }

    public Boolean isValidEmail(@NotNull SendGridValidationResponseBody responseBody) {
        SendGridValidationResponseBody.Result result = responseBody.getResult();
        return  result.hasValidAddressSyntax() &&
                result.hasMxOrARecord() &&
                !result.isSuspectedDisposableAddress() &&
                !result.hasKnownBounces() &&
                !result.hasSuspectedBounces();
    }
}
