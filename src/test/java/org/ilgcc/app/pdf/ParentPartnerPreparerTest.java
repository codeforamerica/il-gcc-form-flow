package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class ParentPartnerPreparerTest {
  private final ParentPartnerPreparer preparer = new ParentPartnerPreparer();

  private Submission submission;

  @Test
  public void partnerProgramHasCompleteStartAndEndDate(){
    submission = new SubmissionTestBuilder()
        .with("partnerProgramStartYear", "2024")
        .with("partnerProgramStartMonth", "10")
        .with("partnerProgramStartDay", "09")
        .with("partnerProgramEndYear", "2025")
        .with("partnerProgramEndMonth", "02")
        .with("partnerProgramEndDay", "10")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("partnerProgramStart")).isEqualTo(new SingleField("partnerProgramStart", "10/09/2024", null));
    assertThat(result.get("partnerProgramEnd")).isEqualTo(new SingleField("partnerProgramEnd", "02/10/2025", null));

  }

  @Test
  public void partnerProgramMissingEndDate(){
    submission = new SubmissionTestBuilder()
        .with("partnerProgramStartYear", "2024")
        .with("partnerProgramStartMonth", "10")
        .with("partnerProgramStartDay", "09")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("partnerProgramStart")).isEqualTo(new SingleField("partnerProgramStart", "10/09/2024", null));
    assertThat(result.get("partnerProgramEnd")).isEqualTo(null);
  }

  @Test
  public void partnerProgramMissingElementOnEndDate(){
    submission = new SubmissionTestBuilder()
        .with("partnerProgramStartYear", "2024")
        .with("partnerProgramStartMonth", "10")
        .with("partnerProgramStartDay", "09")
        .with("partnerProgramStartMonth", "10")
        .with("partnerProgramStartDay", "09")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("partnerProgramStart")).isEqualTo(new SingleField("partnerProgramStart", "10/09/2024", null));
    assertThat(result.get("partnerProgramEnd")).isEqualTo(null);

  }
}