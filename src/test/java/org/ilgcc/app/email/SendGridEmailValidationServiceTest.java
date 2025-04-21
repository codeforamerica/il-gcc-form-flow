package org.ilgcc.app.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import java.util.HashMap;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SendGridValidationResponseBodyBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = IlGCCApplication.class, properties = {
    "sendgrid.email-validation-api-key=fake-api-key",
    "sendgrid.enable-sendgrid-email-validation=true"
})
@ActiveProfiles("test")
public class SendGridEmailValidationServiceTest {

  private final SendGrid mockSendGrid = mock(SendGrid.class);

  private static final String MOCK_VALID_EMAIL = "test@email.com";
  private static final String MOCK_INVALID_EMAIL = "invalid@email.com";
  private static final String ERROR_RESPONSE_BODY = """
          {
            "errors": [
              {
                "field": "email",
                "message": "Invalid email format provided."
              }
            ]
          }
      """;

  @Test
  public void shouldReturnTrueWhenSendGridReturnsValidResponse() throws Exception {
    SendGridEmailValidationService sendGridEmailValidationService = new SendGridEmailValidationService(true, mockSendGrid);
    SendGridEmailValidationService spysendGridEmailValidationService = spy(sendGridEmailValidationService);

    SendGridValidationResponseBody validSendGridResponseBody = new SendGridValidationResponseBodyBuilder()
        .withHasValidAddressSyntax(true)
        .withHasMxOrARecord(true)
        .withIsSuspectedDisposableAddress(false)
        .withHasKnownBounces(false)
        .withHasSuspectedBounces(false)
        .withEmailAddress("test@email.com").build();

    String responseBody = new ObjectMapper().writeValueAsString(validSendGridResponseBody);
    Response fakeResponse = new Response(200, responseBody, new HashMap<>());

    doReturn(fakeResponse).when(spysendGridEmailValidationService).getSendGridResponse(MOCK_VALID_EMAIL);

    HashMap<String, String> result = spysendGridEmailValidationService.validateEmail(MOCK_VALID_EMAIL);
    assertEquals("success", result.get("endpointReached"));
    assertEquals("true", result.get("emailIsValid"));
  }

  @Test
  public void shouldSetEmailIsValidToFalseWhenHasValidSyntaxIsFalse() throws Exception {
    SendGridEmailValidationService sendGridEmailValidationService = new SendGridEmailValidationService(true, mockSendGrid);
    SendGridEmailValidationService spysendGridEmailValidationService = spy(sendGridEmailValidationService);

    SendGridValidationResponseBody validSendGridResponseBody = new SendGridValidationResponseBodyBuilder()
        .withHasValidAddressSyntax(false)
        .withHasMxOrARecord(true)
        .withIsSuspectedDisposableAddress(false)
        .withHasKnownBounces(false)
        .withHasSuspectedBounces(false).withEmailAddress("test@email.com").build();

    String responseBody = new ObjectMapper().writeValueAsString(validSendGridResponseBody);
    Response fakeResponse = new Response(200, responseBody, new HashMap<>());

    doReturn(fakeResponse).when(spysendGridEmailValidationService).getSendGridResponse(MOCK_INVALID_EMAIL);

    HashMap<String, String> result = spysendGridEmailValidationService.validateEmail(MOCK_INVALID_EMAIL);
    assertEquals("success", result.get("endpointReached"));
    assertEquals("false", result.get("emailIsValid"));
  }

  @Test
  public void shouldSetEmailIsValidToFalseWhenHasMXOrARecordIsFalse() throws Exception {
    SendGridEmailValidationService sendGridEmailValidationService = new SendGridEmailValidationService(true, mockSendGrid);
    SendGridEmailValidationService spysendGridEmailValidationService = spy(sendGridEmailValidationService);

    SendGridValidationResponseBody validSendGridResponseBody = new SendGridValidationResponseBodyBuilder()
        .withHasValidAddressSyntax(true)
        .withHasMxOrARecord(false)
        .withIsSuspectedDisposableAddress(false)
        .withHasKnownBounces(false)
        .withHasSuspectedBounces(false).withEmailAddress("test@email.com").build();

    String responseBody = new ObjectMapper().writeValueAsString(validSendGridResponseBody);
    Response fakeResponse = new Response(200, responseBody, new HashMap<>());

    doReturn(fakeResponse).when(spysendGridEmailValidationService).getSendGridResponse(MOCK_INVALID_EMAIL);

    HashMap<String, String> result = spysendGridEmailValidationService.validateEmail(MOCK_INVALID_EMAIL);
    assertEquals("success", result.get("endpointReached"));
    assertEquals("false", result.get("emailIsValid"));
  }

  @Test
  public void shouldSetEmailIsValidToFalseWhenIsSuspectedDisposableAddressIsTrue() throws Exception {
    SendGridEmailValidationService sendGridEmailValidationService = new SendGridEmailValidationService(true, mockSendGrid);
    SendGridEmailValidationService spysendGridEmailValidationService = spy(sendGridEmailValidationService);

    SendGridValidationResponseBody validSendGridResponseBody = new SendGridValidationResponseBodyBuilder()
        .withHasValidAddressSyntax(true)
        .withHasMxOrARecord(true)
        .withIsSuspectedDisposableAddress(true)
        .withHasKnownBounces(false)
        .withHasSuspectedBounces(false).withEmailAddress("test@email.com").build();

    String responseBody = new ObjectMapper().writeValueAsString(validSendGridResponseBody);
    Response fakeResponse = new Response(200, responseBody, new HashMap<>());

    doReturn(fakeResponse).when(spysendGridEmailValidationService).getSendGridResponse(MOCK_INVALID_EMAIL);

    HashMap<String, String> result = spysendGridEmailValidationService.validateEmail(MOCK_INVALID_EMAIL);
    assertEquals("success", result.get("endpointReached"));
    assertEquals("false", result.get("emailIsValid"));
  }

  @Test
  public void shouldSetEmailIsValidToFalseWhenHasKnownBouncesIsTrue() throws Exception {
    SendGridEmailValidationService sendGridEmailValidationService = new SendGridEmailValidationService(true, mockSendGrid);
    SendGridEmailValidationService spysendGridEmailValidationService = spy(sendGridEmailValidationService);

    SendGridValidationResponseBody validSendGridResponseBody = new SendGridValidationResponseBodyBuilder()
        .withHasValidAddressSyntax(true)
        .withHasMxOrARecord(true)
        .withIsSuspectedDisposableAddress(false)
        .withHasKnownBounces(true)
        .withHasSuspectedBounces(false).withEmailAddress("test@email.com").build();

    String responseBody = new ObjectMapper().writeValueAsString(validSendGridResponseBody);
    Response fakeResponse = new Response(200, responseBody, new HashMap<>());

    doReturn(fakeResponse).when(spysendGridEmailValidationService).getSendGridResponse(MOCK_INVALID_EMAIL);

    HashMap<String, String> result = spysendGridEmailValidationService.validateEmail(MOCK_INVALID_EMAIL);
    assertEquals("success", result.get("endpointReached"));
    assertEquals("false", result.get("emailIsValid"));
  }

  @Test
  public void shouldSetEmailToTrueIfAllOtherFieldsAreValidButHasSuspectedBouncesIsTrue() throws Exception {
    SendGridEmailValidationService sendGridEmailValidationService = new SendGridEmailValidationService(true, mockSendGrid);
    SendGridEmailValidationService spysendGridEmailValidationService = spy(sendGridEmailValidationService);

    SendGridValidationResponseBody validSendGridResponseBody = new SendGridValidationResponseBodyBuilder()
        .withHasValidAddressSyntax(true)
        .withHasMxOrARecord(true)
        .withIsSuspectedDisposableAddress(false)
        .withHasKnownBounces(false)
        .withHasSuspectedBounces(true).withEmailAddress("test@email.com").build();

    String responseBody = new ObjectMapper().writeValueAsString(validSendGridResponseBody);
    Response fakeResponse = new Response(200, responseBody, new HashMap<>());

    doReturn(fakeResponse).when(spysendGridEmailValidationService).getSendGridResponse(MOCK_VALID_EMAIL);

    HashMap<String, String> result = spysendGridEmailValidationService.validateEmail(MOCK_VALID_EMAIL);
    assertEquals("success", result.get("endpointReached"));
    assertEquals("true", result.get("emailIsValid"));
  }

  @Test
  public void shouldReturnEmptyHashMapWhenSendGridFlagIsOff() throws Exception {
    SendGridEmailValidationService sendGridEmailValidationService = new SendGridEmailValidationService(false, mockSendGrid);
    SendGridEmailValidationService spysendGridEmailValidationService = spy(sendGridEmailValidationService);

    HashMap<String, String> emailValidationResult = spysendGridEmailValidationService.validateEmail(MOCK_VALID_EMAIL);
    assertTrue(emailValidationResult.isEmpty());
    verify(spysendGridEmailValidationService, never()).getSendGridResponse(MOCK_VALID_EMAIL);
  }

  @Test
  public void shouldSetEndPointReachedToFalseResponseA200ResponseIsNotReturned() throws Exception {
    SendGridEmailValidationService sendGridEmailValidationService = new SendGridEmailValidationService(true, mockSendGrid);
    SendGridEmailValidationService spysendGridEmailValidationService = spy(sendGridEmailValidationService);

    Response fakeResponse = new Response(403, ERROR_RESPONSE_BODY, new HashMap<>());

    doReturn(fakeResponse).when(spysendGridEmailValidationService).getSendGridResponse(MOCK_VALID_EMAIL);

    HashMap<String, String> result = spysendGridEmailValidationService.validateEmail(MOCK_VALID_EMAIL);
    assertEquals("failed", result.get("endpointReached"));
    verify(spysendGridEmailValidationService, never()).isValidEmail(any());
  }
}
