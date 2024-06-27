package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class NeedChildcareForChildrenPreparerTest {
    NeedChildcareForChildren preparer = new NeedChildcareForChildren();

    private Submission submission;

    @Test
    public void shouldSelectTrueOnPDFFieldWhenChildIsAttendingAnyOtherSchoolDuringDay() {
        submission = new SubmissionTestBuilder()
                .withChild("First", "Child", "Yes")
                .withChildAttendsSchoolDuringTheDay(0, "true")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childCareChildInSchool_1")).isEqualTo(new SingleField("childCareChildInSchool", "true", 1));
    }
    @Test
    public void shouldSelectFalseOnPDFFieldWhenChildIsAttendingAnyOtherSchoolDuringDay() {
        submission = new SubmissionTestBuilder()
            .withChild("First", "Child", "Yes")
            .withChildAttendsSchoolDuringTheDay(0, "false")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childCareChildInSchool_1")).isEqualTo(new SingleField("childCareChildInSchool", "false", 1));
    }
    @Test
    public void shouldSelectTrueOnPDFFieldWhenChildIsAUSCitizen() {
        submission = new SubmissionTestBuilder()
            .withChild("First", "Child", "Yes")
            .withChildIsAUSCitizen(0, "Yes")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childUSCitizen_1")).isEqualTo(new SingleField("childUSCitizen", "true", 1));
    }
    @Test
    public void shouldSelectFalseOnPDFFieldWhenChildIsNotAUSCitizen() {
    
        submission = new SubmissionTestBuilder()
            .withChild("First", "Child", "Yes")
            .withChildIsAUSCitizen(0, "No")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childUSCitizen_1")).isEqualTo(new SingleField("childUSCitizen", "false", 1));
    }
    @Test
    public void shouldSelectTrueOnPDFFieldWhenChildHasSpecialNeeds() {
        submission = new SubmissionTestBuilder()
            .withChild("First", "Child", "Yes")
            .withChildHasSpecialNeeds(0, "Yes")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childSpecialNeeds_1")).isEqualTo(new SingleField("childSpecialNeeds", "true", 1));
    }
    @Test
    public void shouldSelectFalseOnPDFFieldWhenChildDoesNotHaveSpecialNeeds() {
        submission = new SubmissionTestBuilder()
            .withChild("First", "Child", "Yes")
            .withChildHasSpecialNeeds(0, "No")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childSpecialNeeds_1")).isEqualTo(new SingleField("childSpecialNeeds", "false", 1));
    }
    @Test
    public void shouldSetCCAPStartDateToTheStartDateOfAChildInNeedOfAssistance(){
        submission = new SubmissionTestBuilder()
            .withChild("Child", "NoChildcare", "No")
            .withChild("Needs", "Childcare", "Yes")
            .addChildCareStartDate(1,"1/1/2019")
            .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "1/1/2019", null));
    }

    @Test
    public void shouldSetCCAPStartDateToTheStartDateOfEarliestCCAPStartDate(){
        submission = new SubmissionTestBuilder()
            .withChild("Child", "NoChildcare", "No")
            .withChild("Needs", "Childcare", "Yes")
            .addChildCareStartDate(0,"1/1/2019")
            .withChild("Second", "Childcare", "Yes")
            .addChildCareStartDate(1,"11/1/2009")
            .withChild("Third", "Childcare", "Yes")
            .addChildCareStartDate(2,"1/12/2024")
            .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "11/1/2009", null));
    }
}
