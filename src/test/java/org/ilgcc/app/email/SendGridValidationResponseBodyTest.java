package org.ilgcc.app.email;

import org.ilgcc.app.utils.SendGridValidationResponseBodyBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SendGridValidationResponseBodyTest {

  private SendGridValidationResponseBody responseBody;

  @BeforeEach
  public void setUp() throws Exception {

    responseBody = new SendGridValidationResponseBodyBuilder()
        .withHasKnownBounces(false)
        .withHasSuspectedBounces(false)
        .withHasValidAddressSyntax(true).withHasMxOrARecord(true)
        .withIsSuspectedDisposableAddress(false)
        .withEmailAddress("test@example.com")
        .withSuggestion("test.corrected@example.com").build();
  }

  @Test
  void shouldReturnCorrectValuesFromDeserializedSendGridResponse() {
    assertNotNull(responseBody.getResult());
    assertEquals("test@example.com", responseBody.getResult().getEmail());
    assertTrue(responseBody.getResult().hasValidAddressSyntax());
    assertFalse(responseBody.getResult().hasKnownBounces());
    assertTrue(responseBody.getResult().hasMxOrARecord());
    assertFalse(responseBody.getResult().hasSuspectedBounces());
  }

  @Test
  void shouldSetHasSuggestionToTrueIfSuggestionIfSuggestionIsPresent() throws Exception {
    assertTrue(responseBody.getResult().hasSuggestedEmailAddress());
    assertEquals("test.corrected@example.com", responseBody.getResult().getSuggestedEmailAddress());
  }
}
