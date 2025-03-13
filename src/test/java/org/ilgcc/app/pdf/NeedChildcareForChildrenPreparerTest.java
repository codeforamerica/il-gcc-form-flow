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
        submission = new SubmissionTestBuilder().withChild("First", "Child", "true").withChild("Second", "Child", "true")
                .withChild("Third", "Child", "true").withChild("Fourth", "Child", "true").withChild("Fifth", "Child", "true")
                .withChild("Sixth", "Child", "true").build();

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
        submission = new SubmissionTestBuilder().withChild("First", "Child", "true").withChildAttendsSchoolDuringTheDay(0, "true")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childCareChildInSchool_1")).isEqualTo(new SingleField("childCareChildInSchool", "true", 1));
    }

    @Test
    public void shouldSelectFalseOnPDFFieldWhenChildIsAttendingAnyOtherSchoolDuringDay() {
        submission = new SubmissionTestBuilder().withChild("First", "Child", "true").withChildAttendsSchoolDuringTheDay(0, "false")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childCareChildInSchool_1")).isEqualTo(new SingleField("childCareChildInSchool", "false", 1));
    }

    @Test
    public void shouldSelectTrueOnPDFFieldWhenChildIsAUSCitizen() {
        submission = new SubmissionTestBuilder().withChild("First", "Child", "true").withChildIsAUSCitizen(0, "Yes").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childUSCitizen_1")).isEqualTo(new SingleField("childUSCitizen", "true", 1));
    }

    @Test
    public void shouldSelectFalseOnPDFFieldWhenChildIsNotAUSCitizen() {

        submission = new SubmissionTestBuilder().withChild("First", "Child", "true").withChildIsAUSCitizen(0, "No").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childUSCitizen_1")).isEqualTo(new SingleField("childUSCitizen", "false", 1));
    }

    @Test
    public void shouldSelectTrueOnPDFFieldWhenChildHasSpecialNeeds() {
        submission = new SubmissionTestBuilder().withChild("First", "Child", "true").withChildHasSpecialNeeds(0, "Yes").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childSpecialNeeds_1")).isEqualTo(new SingleField("childSpecialNeeds", "true", 1));
    }

    @Test
    public void shouldSelectFalseOnPDFFieldWhenChildDoesNotHaveSpecialNeeds() {
        submission = new SubmissionTestBuilder().withChild("First", "Child", "true").withChildHasSpecialNeeds(0, "No").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childSpecialNeeds_1")).isEqualTo(new SingleField("childSpecialNeeds", "false", 1));
    }

    @Test
    public void setCorrectEthnicityRaceKeyInPDF() {
        submission = new SubmissionTestBuilder().withChild("Black Native American", "Child", "true")
                .addChildDataArray(0, "childRaceEthnicity", List.of("BLACK", "NATIVE_AMERICAN"))
                .withChild("White Asian Hispanic", "Child", "true")
                .addChildDataArray(1, "childRaceEthnicity", List.of("WHITE", "BLACK", "ASIAN"))
                .withChild("OTHER MIDDLE EASTERN WHITE", "Child", "true")
                .addChildDataArray(2, "childRaceEthnicity", List.of("HISPANIC", "MIDDLE_EASTERN"))
                .withChild("No Race", "Child", "true").addChildDataArray(3, "childRaceEthnicity", List.of("NONE")).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childRaceEthnicity_1")).isEqualTo(new SingleField("childRaceEthnicity", "2, 5", 1));
        assertThat(result.get("childRaceEthnicity_2")).isEqualTo(new SingleField("childRaceEthnicity", "1, 2, 4", 2));
        assertThat(result.get("childRaceEthnicity_3")).isEqualTo(new SingleField("childRaceEthnicity", "3, O", 3));
        assertThat(result.get("childRaceEthnicity_4")).isEqualTo(new SingleField("childRaceEthnicity", "X", 4));
    }

    @Test
    public void deduplicatesEthnicityRaceKeyInPDF() {
        submission = new SubmissionTestBuilder().withChild("Black Native American", "Child", "true")
                .addChildDataArray(0, "childRaceEthnicity", List.of("OTHER", "MIDDLE_EASTERN")).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childRaceEthnicity_1")).isEqualTo(new SingleField("childRaceEthnicity", "O", 1));
    }

    @Test
    public void ethnicityRaceFieldCanBeEmpty() {
        submission = new SubmissionTestBuilder().withChild("Black Native American", "Child", "true")
                .addChildDataArray(0, "childRaceEthnicity", List.of()).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childRaceEthnicity_1")).isEqualTo(new SingleField("childRaceEthnicity", "", 1));
    }

    @Test
    public void multipleGenderOptionsAreConsolidated() {
        submission = new SubmissionTestBuilder().withChild("All Gender", "Child", "true")
                .addChildDataArray(0, "childGender", List.of("MALE", "FEMALE", "NONBINARY", "TRANSGENDER"))
                .withChild("Male Nonbinary", "Child", "true").addChildDataArray(1, "childGender", List.of("MALE", "NONBINARY"))
                .withChild("Female Transgender", "Child", "true")
                .addChildDataArray(2, "childGender", List.of("FEMALE", "TRANSGENDER"))
                .withChild("No Listed Gender", "Child", "true").addChildDataArray(3, "childGender", List.of("NONE")).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childGender_1")).isEqualTo(new SingleField("childGender", "F,M,NB,T", 1));
        assertThat(result.get("childGender_2")).isEqualTo(new SingleField("childGender", "M,NB", 2));
        assertThat(result.get("childGender_3")).isEqualTo(new SingleField("childGender", "F,T", 3));
        assertThat(result.get("childGender_4")).isEqualTo(new SingleField("childGender", "", 4));
    }

    @Test
    public void genderFieldCanBeEmpty() {
        submission = new SubmissionTestBuilder().withChild("No Gender", "Child", "true")
                .addChildDataArray(0, "childGender", List.of()).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childGender_1")).isEqualTo(new SingleField("childGender", "", 1));
    }

    @Test
    public void childCareChildSchoolHoursIsFilledIfPresent() {
        submission = new SubmissionTestBuilder().withChild("Childcare", "Hours", "true")
            .withChild("Tester", "LastName","true")
            .withDescriptionOfChildAttendsOtherSchoolDuringTheDay(0, "")
            .withDescriptionOfChildAttendsOtherSchoolDuringTheDay(1, "testValue")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childOtherEdHoursDescription_1")).isEqualTo(new SingleField("childOtherEdHoursDescription", "", 1));
        assertThat(result.get("childOtherEdHoursDescription_2")).isEqualTo(new SingleField("childOtherEdHoursDescription", "testValue", 2));

    }
}
