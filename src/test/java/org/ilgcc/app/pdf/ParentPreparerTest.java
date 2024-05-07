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
  public void activitiesProgramMissingElemendOnEndDate(){
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
}