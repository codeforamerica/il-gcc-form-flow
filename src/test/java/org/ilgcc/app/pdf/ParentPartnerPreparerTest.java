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

  @Test
  public void partnerJobsProperlyMapped(){
    submission = new SubmissionTestBuilder()
        .withJob("partnerJobs", "Partner Company One", "123 Partner First Job St", "Chicago", "IL", "60302", "(555) 123-1234")
        .withJob("partnerJobs", "Partner Company Two", "456 Partner Second Job St", "Chicago", "IL", "60302", "(555) 343-1235")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("companyName_1")).isEqualTo(new SingleField("companyName", "Partner Company One", 1));
    assertThat(result.get("employerStreetAddress_1")).isEqualTo(new SingleField("employerStreetAddress", "123 Partner First Job St", 1));
    assertThat(result.get("employerCity_1")).isEqualTo(new SingleField("employerCity", "Chicago", 1));
    assertThat(result.get("employerState_1")).isEqualTo(new SingleField("employerState", "IL", 1));
    assertThat(result.get("employerZipCode_1")).isEqualTo(new SingleField("employerZipCode", "60302", 1));
    assertThat(result.get("employerPhoneNumber_1")).isEqualTo(new SingleField("employerPhoneNumber", "(555) 123-1234", 1));

    assertThat(result.get("companyName_2")).isEqualTo(new SingleField("companyName", "Partner Company Two", 2));
    assertThat(result.get("employerStreetAddress_2")).isEqualTo(new SingleField("employerStreetAddress", "456 Partner Second Job St", 2));
    assertThat(result.get("employerCity_2")).isEqualTo(new SingleField("employerCity", "Chicago", 2));
    assertThat(result.get("employerState_2")).isEqualTo(new SingleField("employerState", "IL", 2));
    assertThat(result.get("employerZipCode_2")).isEqualTo(new SingleField("employerZipCode", "60302", 2));
    assertThat(result.get("employerPhoneNumber_2")).isEqualTo(new SingleField("employerPhoneNumber", "(555) 343-1235", 2));
  }
  @Test
  public void noJobsDoesNotCrash(){
    submission = new SubmissionTestBuilder()
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("companyName_1")).isEqualTo(null);
    assertThat(result.get("employerStreetAddress_1")).isEqualTo(null);
    assertThat(result.get("employerCity_1")).isEqualTo(null);
    assertThat(result.get("employerState_1")).isEqualTo(null);
    assertThat(result.get("employerZipCode_1")).isEqualTo(null);
    assertThat(result.get("employerPhoneNumber_1")).isEqualTo(null);
  }

}