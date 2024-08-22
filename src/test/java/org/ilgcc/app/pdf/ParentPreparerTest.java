package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
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
  public void activitiesProgramDatesAreGeneratedWithMissingDay(){
    submission = new SubmissionTestBuilder()
            .with("activitiesProgramStartYear", "2024")
            .with("activitiesProgramStartMonth", "10")
            .with("activitiesProgramEndYear", "2025")
            .with("activitiesProgramEndMonth", "02")
            .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

    assertThat(result.get("applicantSchoolStartDate")).isEqualTo(new SingleField("applicantSchoolStartDate", "10/2024", null));
    assertThat(result.get("applicantSchoolEndDate")).isEqualTo(new SingleField("applicantSchoolEndDate", "02/2025", null));
  }

  @Test
  public void singleJobsIsProperlyMapped(){
    submission = new SubmissionTestBuilder()
        .withJob("jobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234", "false")
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

  @Test
  public void shouldPreparePrimaryEducationTypeCheckboxFieldWhenSelected(){
    submission = new SubmissionTestBuilder()
        .withParentDetails()
        .withEducationType("highSchoolOrGed", "parent")
        .build();

    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("parentEducation")).isEqualTo(new SingleField("parentEducation", "APPLICANT_EDUCATION_TYPE_HIGH_SCHOOL", null));
  }
  //Paramaterized Test
  //Add in a parent with a gender and write to the correct PDF field
  //GenderTests
  @Test
  public void shouldSelectMaleCheckboxWhenApplicantIdentifiesAsMale(){
    submission = new SubmissionTestBuilder()
        .withParentDetails()
        .with("parentGender[]", List.of("MALE"))
        .build();
    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("parentGenderMale")).isEqualTo(new SingleField("parentGenderMale", "Yes", null));
  }

  @Test
  public void shouldSelectFemaleCheckboxWhenApplicantIdentifiesAsFemale(){
    submission = new SubmissionTestBuilder()
        .withParentDetails()
        .with("parentGender[]", List.of("FEMALE"))
        .build();
    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("parentGenderFemale")).isEqualTo(new SingleField("parentGenderFemale", "Yes", null));
  }

  @Test
  public void shouldWriteTransgenderToTextFieldWhenApplicantIdentifiesAsTransgender(){
    submission = new SubmissionTestBuilder()
        .withParentDetails()
        .with("parentGender[]", List.of("TRANSGENDER"))
        .build();
    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("parentGenderTNB")).isEqualTo(new SingleField("parentGenderTNB", "Transgender", null));
  }

  @Test
  public void shouldWriteNonBinaryToTextFieldWhenApplicantIdentifiesAsNonBinary(){
    submission = new SubmissionTestBuilder()
        .withParentDetails()
        .with("parentGender[]", List.of("NONBINARY"))
        .build();
    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("parentGenderTNB")).isEqualTo(new SingleField("parentGenderTNB", "Nonbinary", null));
  }

  @Test
  public void shouldWriteTransgenderAndNonBinaryAsCommaSeperatedList() {
    submission = new SubmissionTestBuilder()
        .withParentDetails()
        .with("parentGender[]", List.of("TRANSGENDER", "NONBINARY"))
        .build();
    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("parentGenderTNB")).isEqualTo(new SingleField("parentGenderTNB", "Transgender, Nonbinary", null));
  }

  @Test
  public void shouldMapEverythingIfAllGendersAreSelected() {
    submission = new SubmissionTestBuilder()
        .withParentDetails()
        .with("parentGender[]", List.of("MALE", "FEMALE", "NONBINARY", "TRANSGENDER"))
        .build();
    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("parentGenderFemale")).isEqualTo(new SingleField("parentGenderFemale", "Yes", null));
    assertThat(result.get("parentGenderMale")).isEqualTo(new SingleField("parentGenderMale", "Yes", null));
    assertThat(result.get("parentGenderTNB")).isEqualTo(new SingleField("parentGenderTNB", "Transgender, Nonbinary", null));
  }

  @Test
  public void shouldNotMapAnyFieldsIfClientSelectsPreferNotToAnswerToGenderQuestion(){
    submission = new SubmissionTestBuilder()
        .withParentDetails()
        .with("parentGender[]", List.of("NONE", "MALE", "NONBINARY"))
        .build();
    Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
    assertThat(result.get("parentGenderMale")).isNotEqualTo(new SingleField("parentGenderMale", "Yes", null));
    assertThat(result.get("parentGenderTNB")).isNotEqualTo(new SingleField("parentGenderTNB", "Nonbinary", null));


  }
}