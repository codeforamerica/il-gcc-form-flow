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
  public void partnerProgramDatesAreGeneratedWithMissingDay(){
    submission = new SubmissionTestBuilder()
            .with("partnerProgramStartYear", "2024")
            .with("partnerProgramStartMonth", "10")
            .with("partnerProgramEndYear", "2025")
            .with("partnerProgramEndMonth", "02")
            .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("partnerProgramStart")).isEqualTo(new SingleField("partnerProgramStart", "10/2024", null));
    assertThat(result.get("partnerProgramEnd")).isEqualTo(new SingleField("partnerProgramEnd", "02/2025", null));
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
        .withPartnerJob("partnerJobs", "Partner Company One", "123 Partner First Job St", "Chicago", "IL", "60302", "(555) 123-1234", "false")
        .withPartnerJob("partnerJobs", "Partner Company Two", "456 Partner Second Job St", "Chicago", "IL", "60302", "(555) 343-1235","false")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("partnerCompanyName_1")).isEqualTo(new SingleField("partnerCompanyName", "Partner Company One", 1));
    assertThat(result.get("partnerEmployerStreetAddress_1")).isEqualTo(new SingleField("partnerEmployerStreetAddress", "123 Partner First Job St", 1));
    assertThat(result.get("partnerEmployerCity_1")).isEqualTo(new SingleField("partnerEmployerCity", "Chicago", 1));
    assertThat(result.get("partnerEmployerState_1")).isEqualTo(new SingleField("partnerEmployerState", "IL", 1));
    assertThat(result.get("partnerEmployerZipCode_1")).isEqualTo(new SingleField("partnerEmployerZipCode", "60302", 1));
    assertThat(result.get("partnerEmployerPhoneNumber_1")).isEqualTo(new SingleField("partnerEmployerPhoneNumber", "(555) 123-1234", 1));

    assertThat(result.get("partnerCompanyName_2")).isEqualTo(new SingleField("partnerCompanyName", "Partner Company Two", 2));
    assertThat(result.get("partnerEmployerStreetAddress_2")).isEqualTo(new SingleField("partnerEmployerStreetAddress", "456 Partner Second Job St", 2));
    assertThat(result.get("partnerEmployerCity_2")).isEqualTo(new SingleField("partnerEmployerCity", "Chicago", 2));
    assertThat(result.get("partnerEmployerState_2")).isEqualTo(new SingleField("partnerEmployerState", "IL", 2));
    assertThat(result.get("partnerEmployerZipCode_2")).isEqualTo(new SingleField("partnerEmployerZipCode", "60302", 2));
    assertThat(result.get("partnerEmployerPhoneNumber_2")).isEqualTo(new SingleField("partnerEmployerPhoneNumber", "(555) 343-1235", 2));
  }
  @Test
  public void noJobsDoesNotCrash(){
    submission = new SubmissionTestBuilder()
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("partnerCompanyName_1")).isEqualTo(null);
    assertThat(result.get("partnerEmployerStreetAddress_1")).isEqualTo(null);
    assertThat(result.get("partnerEmployerCity_1")).isEqualTo(null);
    assertThat(result.get("partnerEmployerState_1")).isEqualTo(null);
    assertThat(result.get("partnerEmployerZipCode_1")).isEqualTo(null);
    assertThat(result.get("partnerEmployerPhoneNumber_1")).isEqualTo(null);
  }

  @Test
  public void shouldPreparePrimaryEducationTypeCheckboxFieldWhenSelected(){
    submission = new SubmissionTestBuilder()
        .withParentPartnerDetails()
        .withEducationType("internship", "partner")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("partnerEducation")).isEqualTo(new SingleField("partnerEducation", "PARTNER_EDUCATION_TYPE_INTERNSHIP", null));
  }

}