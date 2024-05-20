package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class ParentPreparerTest {
  private final ParentPreparer preparer = new ParentPreparer();

  private Submission submission;

  @Test
  public void activitiesProgramHasCompleteStartAndEndDate(){
    submission = new SubmissionTestBuilder()
        .with("activitiesProgramStartYear", "2024")
        .with("activitiesProgramStartMonth", "10")
        .with("activitiesProgramStartDay", "09")
        .with("activitiesProgramEndYear", "2025")
        .with("activitiesProgramEndMonth", "02")
        .with("activitiesProgramEndDay", "10")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("applicantSchoolStartDate")).isEqualTo(new SingleField("applicantSchoolStartDate", "10/09/2024", null));
    assertThat(result.get("applicantSchoolEndDate")).isEqualTo(new SingleField("applicantSchoolEndDate", "02/10/2025", null));

  }

  @Test
  public void activitiesProgramMissingEndDate(){
    submission = new SubmissionTestBuilder()
        .with("activitiesProgramStartYear", "2024")
        .with("activitiesProgramStartMonth", "10")
        .with("activitiesProgramStartDay", "09")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("applicantSchoolStartDate")).isEqualTo(new SingleField("applicantSchoolStartDate", "10/09/2024", null));
    assertThat(result.get("applicantSchoolEndDate")).isEqualTo(new SingleField("applicantSchoolEndDate", "", null));

  }

  @Test
  public void activitiesProgramMissingElementOnEndDate(){
    submission = new SubmissionTestBuilder()
        .with("activitiesProgramStartYear", "2024")
        .with("activitiesProgramStartMonth", "10")
        .with("activitiesProgramStartDay", "09")
        .with("activitiesProgramStartMonth", "10")
        .with("activitiesProgramStartDay", "09")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("applicantSchoolStartDate")).isEqualTo(new SingleField("applicantSchoolStartDate", "10/09/2024", null));
    assertThat(result.get("applicantSchoolEndDate")).isEqualTo(new SingleField("applicantSchoolEndDate", "", null));

  }

  @Test
  public void singleJobsIsProperlyMapped(){
    submission = new SubmissionTestBuilder()
        .withJob("Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("companyName_1")).isEqualTo(new SingleField("companyName", "Company Name", 1));
    assertThat(result.get("employerStreetAddress_1")).isEqualTo(new SingleField("employerStreetAddress", "123 Main St", 1));
    assertThat(result.get("employerCity_1")).isEqualTo(new SingleField("employerCity", "Springfield", 1));
    assertThat(result.get("employerState_1")).isEqualTo(new SingleField("employerState", "IL", 1));
    assertThat(result.get("employerZipCode_1")).isEqualTo(new SingleField("employerZipCode", "60652", 1));
    assertThat(result.get("employerPhoneNumber_1")).isEqualTo(new SingleField("employerPhoneNumber", "(651) 123-1234", 1));
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