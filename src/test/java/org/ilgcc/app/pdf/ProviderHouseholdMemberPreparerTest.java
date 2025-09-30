package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.pdf.helpers.ProviderHouseholdMemberPreparerHelper;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
        classes = IlGCCApplication.class,
        properties = "il-gcc.enable-multiple-providers=true"

)
@ActiveProfiles("test")
public class ProviderHouseholdMemberPreparerTest {

    ProviderHouseholdMemberPreparerHelper preparer = new ProviderHouseholdMemberPreparerHelper();

    private Submission submission;

    @Test
    public void mapsFirstFiveHouseholdMembersOnly() {
        submission = new SubmissionTestBuilder()
                .withProviderHouseholdMember("First", "Child", "01", "01", "2020", "My Child", "123-12-1234")
                .withProviderHouseholdMember("Second", "Child", "02", "02", "2020", "My Child", "223-12-1234")
                .withProviderHouseholdMember("Third", "Child", "03", "03", "2020", "My Child", "323-12-1234")
                .withProviderHouseholdMember("Fourth", "Child", "04", "04", "2020", "My Child", "423-12-1234")
                .withProviderHouseholdMember("Fifth", "Child", "05", "05", "2020", "My Child", "523-12-1234")
                .withProviderHouseholdMember("Sixth", "Child", "06", "06", "2020", "My Child", "623-12-1234")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission.getInputData());
        assertThat(result.get("providerHouseholdMemberFirstName_1")).isEqualTo(
                new SingleField("providerHouseholdMemberFirstName", "First", 1));
        assertThat(result.get("providerHouseholdMemberLastName_1")).isEqualTo(
                new SingleField("providerHouseholdMemberLastName", "Child", 1));

        assertThat(result.get("providerHouseholdMemberFirstName_2")).isEqualTo(
                new SingleField("providerHouseholdMemberFirstName", "Second", 2));
        assertThat(result.get("providerHouseholdMemberLastName_2")).isEqualTo(
                new SingleField("providerHouseholdMemberLastName", "Child", 2));

        assertThat(result.get("providerHouseholdMemberFirstName_3")).isEqualTo(
                new SingleField("providerHouseholdMemberFirstName", "Third", 3));
        assertThat(result.get("providerHouseholdMemberLastName_3")).isEqualTo(
                new SingleField("providerHouseholdMemberLastName", "Child", 3));

        assertThat(result.get("providerHouseholdMemberFirstName_4")).isEqualTo(
                new SingleField("providerHouseholdMemberFirstName", "Fourth", 4));
        assertThat(result.get("providerHouseholdMemberLastName_4")).isEqualTo(
                new SingleField("providerHouseholdMemberLastName", "Child", 4));

        assertThat(result.get("providerHouseholdMemberFirstName_5")).isEqualTo(
                new SingleField("providerHouseholdMemberFirstName", "Fifth", 5));
        assertThat(result.get("providerHouseholdMemberLastName_5")).isEqualTo(
                new SingleField("providerHouseholdMemberLastName", "Child", 5));

        // We only map the first 5 household members, so #6 is null
        assertThat(result.get("providerHouseholdMemberFirstName_6")).isNull();
        assertThat(result.get("providerHouseholdMemberLastName_6")).isNull();
    }
}