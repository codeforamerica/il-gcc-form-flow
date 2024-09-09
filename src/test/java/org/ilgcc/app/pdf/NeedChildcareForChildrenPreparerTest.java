package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class NeedChildcareForChildrenPreparerTest {

    NeedChildcareForChildren preparer = new NeedChildcareForChildren();

    private Submission submission;

    @Test
    public void includesOnlyFirstFourChildrenNeedingAssistance() {
        submission = new SubmissionTestBuilder().withChild("First", "Child", "Yes").withChild("Second", "Child", "Yes")
                .withChild("Third", "Child", "Yes").withChild("Fourth", "Child", "Yes").withChild("Fifth", "Child", "Yes")
                .withChild("Sixth", "Child", "Yes").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childFirstName_1")).isEqualTo(new SingleField("childFirstName", "First", 1));
        assertThat(result.get("childFirstName_2")).isEqualTo(new SingleField("childFirstName", "Second", 2));
        assertThat(result.get("childFirstName_3")).isEqualTo(new SingleField("childFirstName", "Third", 3));
        assertThat(result.get("childFirstName_4")).isEqualTo(new SingleField("childFirstName", "Fourth", 4));
        assertThat(result.get("childFirstName_5")).isEqualTo(null);
        assertThat(result.get("childFirstName_6")).isEqualTo(null);
    }

    @Test
    public void shouldSelectTrueOnPDFFieldWhenChildIsAttendingAnyOtherSchoolDuringDay() {
        submission = new SubmissionTestBuilder().withChild("First", "Child", "Yes").withChildAttendsSchoolDuringTheDay(0, "true")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childCareChildInSchool_1")).isEqualTo(new SingleField("childCareChildInSchool", "true", 1));
    }

    @Test
    public void shouldSelectFalseOnPDFFieldWhenChildIsAttendingAnyOtherSchoolDuringDay() {
        submission = new SubmissionTestBuilder().withChild("First", "Child", "Yes").withChildAttendsSchoolDuringTheDay(0, "false")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childCareChildInSchool_1")).isEqualTo(new SingleField("childCareChildInSchool", "false", 1));
    }

    @Test
    public void shouldSelectTrueOnPDFFieldWhenChildIsAUSCitizen() {
        submission = new SubmissionTestBuilder().withChild("First", "Child", "Yes").withChildIsAUSCitizen(0, "Yes").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childUSCitizen_1")).isEqualTo(new SingleField("childUSCitizen", "true", 1));
    }

    @Test
    public void shouldSelectFalseOnPDFFieldWhenChildIsNotAUSCitizen() {

        submission = new SubmissionTestBuilder().withChild("First", "Child", "Yes").withChildIsAUSCitizen(0, "No").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childUSCitizen_1")).isEqualTo(new SingleField("childUSCitizen", "false", 1));
    }

    @Test
    public void shouldSelectTrueOnPDFFieldWhenChildHasSpecialNeeds() {
        submission = new SubmissionTestBuilder().withChild("First", "Child", "Yes").withChildHasSpecialNeeds(0, "Yes").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childSpecialNeeds_1")).isEqualTo(new SingleField("childSpecialNeeds", "true", 1));
    }

    @Test
    public void shouldSelectFalseOnPDFFieldWhenChildDoesNotHaveSpecialNeeds() {
        submission = new SubmissionTestBuilder().withChild("First", "Child", "Yes").withChildHasSpecialNeeds(0, "No").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childSpecialNeeds_1")).isEqualTo(new SingleField("childSpecialNeeds", "false", 1));
    }

    @Test
    public void shouldSetCCAPStartDateToTheStartDateOfAChildInNeedOfAssistance() {
        submission = new SubmissionTestBuilder().withChild("Child", "NoChildcare", "No").withChild("Needs", "Childcare", "Yes")
                .addChildCareStartDate(1, "1/1/2019").build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "1/1/2019", null));
    }

    @Test
    public void shouldSetCCAPStartDateToTheStartDateOfEarliestCCAPStartDate() {
        submission = new SubmissionTestBuilder().withChild("Child", "NoChildcare", "No").withChild("Needs", "Childcare", "Yes")
                .addChildCareStartDate(0, "1/1/2019").withChild("Second", "Childcare", "Yes")
                .addChildCareStartDate(1, "11/1/2009").withChild("Third", "Childcare", "Yes")
                .addChildCareStartDate(2, "1/12/2024").build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "11/1/2009", null));
    }

    @Test
    public void setCorrectEthnicityRaceKeyInPDF() {
        submission = new SubmissionTestBuilder().withChild("Black Native American", "Child", "Yes")
                .addChildDataArray(0, "childRaceEthnicity", List.of("BLACK", "NATIVE_AMERICAN"))
                .withChild("White Asian Hispanic", "Child", "Yes")
                .addChildDataArray(1, "childRaceEthnicity", List.of("WHITE", "BLACK", "ASIAN"))
                .withChild("OTHER MIDDLE EASTERN WHITE", "Child", "Yes")
                .addChildDataArray(2, "childRaceEthnicity", List.of("HISPANIC", "MIDDLE_EASTERN"))
                .withChild("No Race", "Child", "Yes").addChildDataArray(3, "childRaceEthnicity", List.of("NONE")).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childRaceEthnicity_1")).isEqualTo(new SingleField("childRaceEthnicity", "2, 5", 1));
        assertThat(result.get("childRaceEthnicity_2")).isEqualTo(new SingleField("childRaceEthnicity", "1, 2, 4", 2));
        assertThat(result.get("childRaceEthnicity_3")).isEqualTo(new SingleField("childRaceEthnicity", "3, O", 3));
        assertThat(result.get("childRaceEthnicity_4")).isEqualTo(new SingleField("childRaceEthnicity", "X", 4));
    }

    @Test
    public void deduplicatesEthnicityRaceKeyInPDF() {
        submission = new SubmissionTestBuilder().withChild("Black Native American", "Child", "Yes")
                .addChildDataArray(0, "childRaceEthnicity", List.of("OTHER", "MIDDLE_EASTERN")).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childRaceEthnicity_1")).isEqualTo(new SingleField("childRaceEthnicity", "O", 1));
    }

    @Test
    public void ethnicityRaceFieldCanBeEmpty() {
        submission = new SubmissionTestBuilder().withChild("Black Native American", "Child", "Yes")
                .addChildDataArray(0, "childRaceEthnicity", List.of()).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childRaceEthnicity_1")).isEqualTo(new SingleField("childRaceEthnicity", "", 1));
    }

    @Test
    public void multipleGenderOptionsAreConsolidated() {
        submission = new SubmissionTestBuilder().withChild("All Gender", "Child", "Yes")
                .addChildDataArray(0, "childGender", List.of("MALE", "FEMALE", "NONBINARY", "TRANSGENDER"))
                .withChild("Male Nonbinary", "Child", "Yes").addChildDataArray(1, "childGender", List.of("MALE", "NONBINARY"))
                .withChild("Female Transgender", "Child", "Yes")
                .addChildDataArray(2, "childGender", List.of("FEMALE", "TRANSGENDER"))
                .withChild("No Listed Gender", "Child", "Yes").addChildDataArray(3, "childGender", List.of("NONE")).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childGender_1")).isEqualTo(new SingleField("childGender", "F,M,NB,T", 1));
        assertThat(result.get("childGender_2")).isEqualTo(new SingleField("childGender", "M,NB", 2));
        assertThat(result.get("childGender_3")).isEqualTo(new SingleField("childGender", "F,T", 3));
        assertThat(result.get("childGender_4")).isEqualTo(new SingleField("childGender", "", 4));
    }

    @Test
    public void genderFieldCanBeEmpty() {
        submission = new SubmissionTestBuilder().withChild("No Gender", "Child", "Yes")
                .addChildDataArray(0, "childGender", List.of()).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childGender_1")).isEqualTo(new SingleField("childGender", "", 1));
    }
}
