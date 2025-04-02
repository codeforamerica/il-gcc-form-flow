package org.ilgcc.app.submission.conditions;

import static org.assertj.core.api.Assertions.assertThat;


import formflow.library.data.Submission;
import java.util.List;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
@SpringBootTest(
    classes = IlGCCApplication.class,
    properties = "il-gcc.enable-provider-messaging=true"
)

@ActiveProfiles("test")
public class DisplaySubmitContactProviderTextScreenTest {

  @Autowired
  DisplaySubmitContactProviderTextScreen condition;

  private final String phoneNumberInputName = "familyIntendedProviderPhoneNumber";

  private final String contactMethodInputName = "contactProviderMethod[]";

  @Test
  public void skipsScreenIfContactProviderMethodIsNotEmail() {
    Submission submission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .with(contactMethodInputName, List.of("OTHER"))
        .with(phoneNumberInputName, "")
        .build();

    assertThat(condition.run(submission)).isEqualTo(false);
  }

  @Test
  public void showsScreenIfContactProviderMethodIsTextAndPhoneNumberInputIsBlank() {
    Submission submission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .with(contactMethodInputName, List.of("TEXT"))
        .with(phoneNumberInputName, "")
        .build();

    assertThat(condition.run(submission)).isEqualTo(true);
  }

  @Test
  public void showsScreenIfContactProviderMethodIsTextAndPhoneNumberInputIsMissing() {
    Submission submission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .with(contactMethodInputName, List.of("TEXT"))
        .build();

    assertThat(condition.run(submission)).isEqualTo(true);
  }

  @Test
  public void skipsScreenIfContactProviderMethodIsEmailAndEmailInputIsNotBlank() {
    Submission submission = new SubmissionTestBuilder()
        .withFlow("gcc")
        .with(contactMethodInputName, List.of("EMAIL"))
        .with(phoneNumberInputName, "(333)333-3333")
        .build();

    assertThat(condition.run(submission)).isEqualTo(false);
  }
}

