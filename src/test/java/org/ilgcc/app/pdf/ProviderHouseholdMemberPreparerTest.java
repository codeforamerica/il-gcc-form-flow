package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class
)
@ActiveProfiles("test")
public class ProviderHouseholdMemberPreparerTest {


    @Autowired
    private ProviderHouseholdMemberPreparer preparer;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    private Submission familySubmission;
    private Submission providerSubmission;

    @Test
    public void shouldPrintProviderHouseholdMembersDateOfBirthToPDFIfMonthDayAndYearIsPresentAndIterationIsComplete() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderHouseholdMember("1-Iteration",
                    "One",
                    "30",
                    "10",
                    "2004",
                    "brother",
                    "555-555-5555")

                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerHouseholdMemberDateOfBirth_1")).isEqualTo(new SingleField("providerHouseholdMemberDateOfBirth", "10/30/2004", 1));
    }
    @Test
    public void shouldPrintMultipleProviderHouseholdMembersInfoToPDFFields() {
        providerSubmission = new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .withProviderSubmissionData()
            .withProviderHouseholdMember("1-Iteration",
                "One",
                "30",
                "10",
                "2004",
                "brother",
                "555-55-5555")
            .withProviderHouseholdMember("2-Iteration",
                "Two",
                "28",
                "6",
                "2000",
                "Sister",
                "444-55-5555")
            .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
            .withFlow("gcc")
            .with("providerResponseSubmissionId", providerSubmission.getId())
            .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerHouseholdMemberFirstName_1")).isEqualTo(new SingleField("providerHouseholdMemberFirstName", "1-Iteration", 1));
        assertThat(result.get("providerHouseholdMemberLastName_1")).isEqualTo(new SingleField("providerHouseholdMemberLastName", "One", 1));
        assertThat(result.get("providerHouseholdMemberRelationship_1")).isEqualTo(new SingleField("providerHouseholdMemberRelationship", "brother", 1));
        assertThat(result.get("providerHouseholdMemberSSN_1")).isEqualTo(new SingleField("providerHouseholdMemberSSN", "555-55-5555", 1));
        assertThat(result.get("providerHouseholdMemberDateOfBirth_1")).isEqualTo(new SingleField("providerHouseholdMemberDateOfBirth", "10/30/2004", 1));

        assertThat(result.get("providerHouseholdMemberFirstName_2")).isEqualTo(new SingleField("providerHouseholdMemberFirstName", "2-Iteration", 2));
        assertThat(result.get("providerHouseholdMemberLastName_2")).isEqualTo(new SingleField("providerHouseholdMemberLastName", "Two", 2));
        assertThat(result.get("providerHouseholdMemberRelationship_2")).isEqualTo(new SingleField("providerHouseholdMemberRelationship", "Sister", 2));
        assertThat(result.get("providerHouseholdMemberSSN_2")).isEqualTo(new SingleField("providerHouseholdMemberSSN", "444-55-5555", 2));
        assertThat(result.get("providerHouseholdMemberDateOfBirth_2")).isEqualTo(new SingleField("providerHouseholdMemberDateOfBirth", "6/28/2000", 2));
    }
}
