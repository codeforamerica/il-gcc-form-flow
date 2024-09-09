package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class OtherFamilyMembersPreparerTest {

    OtherFamilyMembersPreparer preparer = new OtherFamilyMembersPreparer();

    private Submission submission;

    @Test
    public void includesChildrenNeedingAssistanceBeyondFourAllowed() {
        submission = new SubmissionTestBuilder()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .withChild("Third", "Child", "Yes")
                .withChild("Fourth", "Child", "Yes")
                .withChild("Fifth", "Child", "Yes")
                .withChild("Sixth", "Child", "Yes")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("familyMemberFirstName_1")).isEqualTo(new SingleField("familyMemberFirstName", "Fifth", 1));
        assertThat(result.get("familyMemberLastName_1")).isEqualTo(
                new SingleField("familyMemberLastName", "Child (Needs CCAP)", 1));

        assertThat(result.get("familyMemberFirstName_2")).isEqualTo(new SingleField("familyMemberFirstName", "Sixth", 2));
        assertThat(result.get("familyMemberLastName_2")).isEqualTo(
                new SingleField("familyMemberLastName", "Child (Needs CCAP)", 2));

    }

    @Test
    public void correctlyPreparesChildAndAdultInformation() {
        submission = new SubmissionTestBuilder()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .withChild("Third", "Child", "Yes")
                .withChild("Fourth", "Child", "Yes")
                .withChild("Fifth", "Child", "Yes")
                .withChild("Sixth", "Child", "No")
                .withChild("Seventh", "Child", "No")
                .withChild("Eight", "Child", "No")
                .withAdultDependent("First A", "Adult-Dependent")
                .withAdultDependent("Second A", "Adult-Dependent")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("familyMemberFirstName_1")).isEqualTo(new SingleField("familyMemberFirstName", "Fifth", 1));
        assertThat(result.get("familyMemberLastName_1")).isEqualTo(
                new SingleField("familyMemberLastName", "Child (Needs CCAP)", 1));

        assertThat(result.get("familyMemberFirstName_2")).isEqualTo(new SingleField("familyMemberFirstName", "Sixth", 2));
        assertThat(result.get("familyMemberLastName_2")).isEqualTo(new SingleField("familyMemberLastName", "Child", 2));

        assertThat(result.get("familyMemberFirstName_3")).isEqualTo(new SingleField("familyMemberFirstName", "Seventh", 3));
        assertThat(result.get("familyMemberLastName_3")).isEqualTo(new SingleField("familyMemberLastName", "Child", 3));

        assertThat(result.get("familyMemberFirstName_4")).isEqualTo(new SingleField("familyMemberFirstName", "Eight", 4));
        assertThat(result.get("familyMemberLastName_4")).isEqualTo(new SingleField("familyMemberLastName", "Child", 4));

        assertThat(result.get("familyMemberFirstName_5")).isEqualTo(new SingleField("familyMemberFirstName", "First A", 5));
        assertThat(result.get("familyMemberLastName_5")).isEqualTo(new SingleField("familyMemberLastName", "Adult-Dependent", 5));

        assertThat(result.get("familyMemberFirstName_6")).isEqualTo(new SingleField("familyMemberFirstName", "Second A", 6));
        assertThat(result.get("familyMemberLastName_6")).isEqualTo(new SingleField("familyMemberLastName", "Adult-Dependent", 6));

    }
}