package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.utils.enums.ProviderType.*;

import formflow.library.data.Submission;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.submission.conditions.ProviderDOBRequired;
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
public class ProviderDOBRequiredUnitTest {
  @Autowired
  private ProviderDOBRequired providerDOBRequired;
  private Submission submission;


  @Test
  public void shouldReturnTrueWhenProviderRegistrationFlowFlagIsOnAndProviderTypeIsLicensedExemptRelativeInProviderHome() {
    submission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("providerType", LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME.name())
        .build();
    assertThat(providerDOBRequired.run(submission)).isEqualTo(true);
  }

  @Test
  public void shouldReturnTrueWhenProviderRegistrationFlowFlagIsOnAndProviderTypeIsLicensedExemptNonRelativeInProviderHome(){
    submission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("providerType", LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME.name())
        .build();
    assertThat(providerDOBRequired.run(submission)).isEqualTo(true);
  }
  @Test
  public void shouldReturnTrueWhenProviderRegistrationFlowFlagIsOnAndProviderTypeIsLicensedExemptRelativeInChildsHome() {
    submission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("providerType", LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name())
        .build();
    assertThat(providerDOBRequired.run(submission)).isEqualTo(true);
  }

  @Test
  public void shouldReturnTrueWhenProviderRegistrationFlowFlagIsOnAndProviderTypeIsLicensedExemptNonRelativeInChildsHome(){
    submission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("providerType", LICENSE_EXEMPT_NONRELATIVE_IN_CHILDS_HOME.name())
        .build();
    assertThat(providerDOBRequired.run(submission)).isEqualTo(true);
  }
  @Test
  public void shouldReturnFalseWhenProviderTypeDoesNotRequireDOB(){
    submission = new SubmissionTestBuilder()
        .withFlow("providerresponse")
        .with("providerType", LICENSE_EXEMPT_CHILD_CARE_CENTER.name())
        .build();
    assertThat(providerDOBRequired.run(submission)).isEqualTo(false);
  }
}
