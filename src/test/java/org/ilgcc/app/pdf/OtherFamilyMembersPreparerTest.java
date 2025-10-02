package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
public class OtherFamilyMembersPreparerTest {

    OtherFamilyMembersPreparer preparer = new OtherFamilyMembersPreparer();

    private Submission submission;

    @Test
    public void includesChildrenNeedingAssistanceBeyondFourAllowed() {
        submission = new SubmissionTestBuilder()
                .withChild("First", "Child", "true", true)
                .withChild("Second", "Child", "true", true)
                .withChild("Third", "Child", "true", true)
                .withChild("Fourth", "Child", "true", true)
                .withChild("Fifth", "Child", "true", true)
                .withChild("Sixth", "Child", "true", true)
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
                .withChild("First", "Child", "true", true)
                .withChild("Second", "Child", "true", true)
                .withChild("Third", "Child", "true", true)
                .withChild("Fourth", "Child", "true", true)
                .withChild("Fifth", "Child", "true", true)
                .withChild("Sixth", "Child", "false", true)
                .withChild("Seventh", "Child", "false", true)
                .withChild("Eight", "Child", "false", true)
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

        // We only map the first 5 family members, so #6 is null
        assertThat(result.get("familyMemberFirstName_6")).isNull();
        assertThat(result.get("familyMemberLastName_6")).isNull();
    }
}