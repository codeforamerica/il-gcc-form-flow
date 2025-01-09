package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;


import formflow.library.data.Submission;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.submission.conditions.NeedsSSNForTaxID;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = IlGCCApplication.class,
    properties = "il-gcc.allow-provider-registration-flow=true"
)

@ActiveProfiles("test")
public class NeedSSNForTaxIdTest {
  @Autowired
  private NeedsSSNForTaxID needsSSNForTaxID;
  private Submission submission;


  @Test
  public void shouldReturnTrueWhenProviderRegistrationFlowFlagIsOnProviderTaxIdTypeIsSSNAndProviderIdentityCheckSSNIsEmpty() {
    submission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("providerTaxIdType", "SSN")
        .build();
    assertThat(needsSSNForTaxID.run(submission)).isEqualTo(true);
  }

  @Test
  public void shouldReturnFalseWhenProviderTaxIdIsSSNAndProviderIdentityCheckSSNHasSSN() {
    submission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("providerTaxIdType", "SSN")
        .with("providerIdentityCheckSSN", "333-33-3333")
        .build();
    assertThat(needsSSNForTaxID.run(submission)).isEqualTo(false);
  }
  @Test
  public void shouldReturnFalseWhenProviderTaxIdIsNotSSN() {
    submission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("providerTaxIdType", "FEIN")
        .build();
    assertThat(needsSSNForTaxID.run(submission)).isEqualTo(false);
  }
}
