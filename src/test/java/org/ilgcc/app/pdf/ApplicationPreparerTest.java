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
  public void addsPartnerSignatureDateIfPartnerSignatureExists() {
    submission = new SubmissionTestBuilder()
        .with("partnerSignedName", "PartnerSignature")
        .withSubmittedAtDate(OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)))
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("partnerSignedAt")).isEqualTo(new SingleField("partnerSignedAt", "10/11/2022", null));
  }

  @Test
  public void doesNotAddPartnerSignatureDateIfPartnerSignatureBlank() {
    submission = new SubmissionTestBuilder()
        .withSubmittedAtDate(OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)))
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("partnerSignedAt")).isEqualTo(null);
  }

  @Test
  public void setsOtherMonthlyIncomeToZeroWhenNoUnearnedIncome(){
    submission = new SubmissionTestBuilder().build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("otherMonthlyIncomeApplicant")).isEqualTo(new SingleField("otherMonthlyIncomeApplicant", "0", null));
  }

  @Test
  public void setsOtherMonthlyIncomeByAddingUnearnedIncome(){
    submission = new SubmissionTestBuilder()
        .with("unearnedIncomeRental", "123")
        .with("unearnedIncomeDividends", "127")
        .with("unearnedIncomeUnemployment", "124")
        .with("unearnedIncomeRoyalties", "126")
        .with("unearnedIncomePension", "122")
        .with("unearnedIncomeWorkers", "128")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("otherMonthlyIncomeApplicant")).isEqualTo(new SingleField("otherMonthlyIncomeApplicant", "750", null));
  }

  @Test
  public void setsOtherMonthlyIncomeWithEmptyStringValue(){
    submission = new SubmissionTestBuilder()
        .with("unearnedIncomeRental", "")
        .with("unearnedIncomeDividends", "")
        .with("unearnedIncomeUnemployment", "124")
        .with("unearnedIncomeRoyalties", "126")
        .with("unearnedIncomePension", "")
        .with("unearnedIncomeWorkers", "")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("otherMonthlyIncomeApplicant")).isEqualTo(new SingleField("otherMonthlyIncomeApplicant", "250", null));
  }
}