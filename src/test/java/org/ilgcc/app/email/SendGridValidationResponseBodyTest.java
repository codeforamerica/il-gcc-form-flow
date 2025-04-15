package org.ilgcc.app.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SendGridValidationResponseBodyTest {

  private SendGridValidationResponseBody body;

  @BeforeEach
  public void setUp() throws Exception {
    String sendGridResponseBody = """
        {
          "result": {
            "email": "test@example.com",
            "verdict": "Valid",
            "score": 0.99,
            "checks": {
              "domain": {
                "has_valid_address_syntax": true,
                "has_mx_or_a_record": true,
                "is_suspected_disposable_address": false
              },
              "additional": {
                "has_known_bounces": false,
                "has_suspected_bounces": false
              }
            },
            "suggestion": "test.corrected@example.com"
          }
        }
        """;
    ObjectMapper mapper = new ObjectMapper();
    body = mapper.readValue(sendGridResponseBody, SendGridValidationResponseBody.class);
  }

  @Test
  void shouldReturnCorrectValuesFromDeserializedSendGridResponse() {
    assertNotNull(body.getResult());
    assertEquals("test@example.com", body.getResult().getEmail());
    assertTrue(body.getResult().hasValidAddressSyntax());
    assertFalse(body.getResult().hasKnownBounces());
    assertTrue(body.getResult().hasMxOrARecord());
    assertFalse(body.getResult().hasSuspectedBounces());
  }

  @Test
  void shouldSetHasSuggestionToTrueIfSuggestionIfSuggestionIsPresent() throws Exception {
    assertTrue(body.getResult().hasSuggestedEmailAddress());
    assertEquals("test.corrected@example.com", body.getResult().getSuggestedEmailAddress());
  }
}
