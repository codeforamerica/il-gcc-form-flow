package org.ilgcc.app.utils;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
public class EmailUtilityTest {
  @Test
  public void shouldGenerateValidMailtoAddress(){
    String validMailtoAddress = "mailto:nowhere@mozilla.org?subject=The%20subject%20of%20the%20email&body=The%20body%20of%20the%20email";
    String emailAddress="nowhere@mozilla.org";
    String subject="The subject of the email";
    String emailBody = "The body of the email";

    assertThat(EmailUtilities.generateWellFormedEmail(emailAddress, subject, emailBody)).isEqualTo(validMailtoAddress);
  }
}
