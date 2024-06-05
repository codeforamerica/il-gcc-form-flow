package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class ApplicationPreparerTest {
  private final ApplicationPreparer preparer = new ApplicationPreparer();

  private Submission submission;

  @Test
  public void addsPartnerSignatureDateIfPartnerSignatureExists(){
    submission = new SubmissionTestBuilder()
        .with("partnerSignedName", "PartnerSignature")
        .withSubmittedAtDate(OffsetDateTime.of(2022,10,11,0,0,0,0, ZoneOffset.ofTotalSeconds(0)))
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("partnerSignedAt")).isEqualTo(new SingleField("partnerSignedAt", "10/11/2022", null));
  }

  @Test
  public void doesNotAddPartnerSignatureDateIfPartnerSignatureBlank(){
    submission = new SubmissionTestBuilder()
        .withSubmittedAtDate(OffsetDateTime.of(2022,10,11,0,0,0,0, ZoneOffset.ofTotalSeconds(0)))
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("partnerSignedAt")).isEqualTo(null);
  }
}