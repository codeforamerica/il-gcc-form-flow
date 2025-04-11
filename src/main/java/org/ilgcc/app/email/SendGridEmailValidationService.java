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

  public Boolean validateEmail(String emailAddress) throws IOException {
        Response response = getSendGridResponse(emailAddress);
        ObjectMapper mapper = new ObjectMapper();
        SendGridValidationResponseBody responseBody = mapper.readValue(response.getBody(), SendGridValidationResponseBody.class);
        if (isValidEmail(responseBody)){
            return true;
        }else {
            return false;
        }
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

    public Boolean sendGridEmailValidationIsAvailable(String emailAddress) throws IOException {
      if  (ENABLE_EMAIL_VALIDATION) {
          Response response = getSendGridResponse(emailAddress);
          return response.getStatusCode() == 200;
      }  else {
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
