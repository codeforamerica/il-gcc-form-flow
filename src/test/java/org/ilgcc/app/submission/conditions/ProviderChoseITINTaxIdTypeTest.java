package org.ilgcc.app.submission.conditions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class ProviderChoseITINTaxIdTypeTest {
  Submission submission;
  ProviderChoseITINTaxIdType providerChoseITINTaxIdType;
  @Test
  void shouldReturnTrueIfITINIsPreferredTaxIDTypeAndProviderHasProviderITINIsEmpty(){
    submission = new SubmissionTestBuilder().with("providerITIN", "").with("providerTaxIdType", "ITIN").build();
    providerChoseITINTaxIdType = new ProviderChoseITINTaxIdType();
    assertThat(providerChoseITINTaxIdType.run(submission)).isTrue();
  }

  @Test
  void shouldReturnFalseIfITINIsPreferredTaxIDTypeAndProviderHasProviderITINIsNotEmpty(){
    submission = new SubmissionTestBuilder().with("providerITIN", "999999999").with("providerTaxIdType", "ITIN").build();
    providerChoseITINTaxIdType = new ProviderChoseITINTaxIdType();
    assertThat(providerChoseITINTaxIdType.run(submission)).isFalse();
  }

  @Test
  void shouldReturnFalseIfITINIsNotPreferredTaxIDType(){
    submission = new SubmissionTestBuilder().with("providerITIN", "").with("providerTaxIdType", "SSN").build();
    providerChoseITINTaxIdType = new ProviderChoseITINTaxIdType();
    assertThat(providerChoseITINTaxIdType.run(submission)).isFalse();
  }
}