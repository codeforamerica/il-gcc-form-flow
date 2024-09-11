package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

public class ApplicationPreparerTest {

    private final ApplicationPreparer preparer = new ApplicationPreparer();

    private Submission submission;

    @Test
    public void addsPartnerSignatureDateIfPartnerSignatureExists() {
        submission = new SubmissionTestBuilder().with("partnerSignedName", "PartnerSignature")
                .withSubmittedAtDate(OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))).build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("partnerSignedAt")).isEqualTo(new SingleField("partnerSignedAt", "10/11/2022", null));
    }

    @Test
    public void doesNotAddPartnerSignatureDateIfPartnerSignatureBlank() {
        submission = new SubmissionTestBuilder().withSubmittedAtDate(
                OffsetDateTime.of(2022, 10, 11, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))).build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("partnerSignedAt")).isEqualTo(null);
    }

    @Test
    public void setsOtherMonthlyIncomeToZeroWhenNoUnearnedIncome() {
        submission = new SubmissionTestBuilder().build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("otherMonthlyIncomeApplicant")).isEqualTo(
                new SingleField("otherMonthlyIncomeApplicant", "0", null));
    }

    @Test
    public void setsOtherMonthlyIncomeByAddingUnearnedIncome() {
        submission = new SubmissionTestBuilder().with("unearnedIncomeRental", "123").with("unearnedIncomeDividends", "127")
                .with("unearnedIncomeUnemployment", "124").with("unearnedIncomeRoyalties", "126")
                .with("unearnedIncomePension", "122").with("unearnedIncomeWorkers", "128").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("otherMonthlyIncomeApplicant")).isEqualTo(
                new SingleField("otherMonthlyIncomeApplicant", "750", null));
    }

    @Test
    public void setsOtherMonthlyIncomeWithEmptyStringValue() {
        submission = new SubmissionTestBuilder().with("unearnedIncomeRental", "").with("unearnedIncomeDividends", "")
                .with("unearnedIncomeUnemployment", "124").with("unearnedIncomeRoyalties", "126")
                .with("unearnedIncomePension", "").with("unearnedIncomeWorkers", "").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("otherMonthlyIncomeApplicant")).isEqualTo(
                new SingleField("otherMonthlyIncomeApplicant", "250", null));
    }

    @Test
    public void roundsDownOtherMonthlyIncome() {
        submission = new SubmissionTestBuilder().with("unearnedIncomeRental", "").with("unearnedIncomeDividends", "")
                .with("unearnedIncomeUnemployment", "124.33").with("unearnedIncomeRoyalties", "126.66")
                .with("unearnedIncomePension", "").with("unearnedIncomeWorkers", "").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("otherMonthlyIncomeApplicant")).isEqualTo(
                new SingleField("otherMonthlyIncomeApplicant", "250", null));
    }

    @Test
    public void doesNotAddReceivedTimestampIfSubmittedAtBlank() {
        submission = new SubmissionTestBuilder().build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("receivedTimestamp")).isEqualTo(null);
    }


    @Test
    public void receivedTimestampIsFormattedCorrectlyInDaylightTime() {
        submission = new SubmissionTestBuilder().withSubmittedAtDate(
                OffsetDateTime.of(2024, 6, 6, 23, 47, 0, 0, ZoneOffset.of("+00:00"))).build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("receivedTimestamp")).isEqualTo(
                new SingleField("receivedTimestamp", "June 6, 2024, 6:47 PM CDT", null));
    }

    @Test
    public void receivedTimestampIsFormattedCorrectlyInStandardTime() {
        submission = new SubmissionTestBuilder().withSubmittedAtDate(
                OffsetDateTime.of(2024, 12, 6, 23, 47, 0, 0, ZoneOffset.of("+00:00"))).build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("receivedTimestamp")).isEqualTo(
                new SingleField("receivedTimestamp", "December 6, 2024, 5:47 PM CST", null));
    }

    @Test
    public void addsParentFullName() {
        submission = new SubmissionTestBuilder().with("parentFirstName", "Lily-Mae").with("parentLastName", "Stone").build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("parentFullName")).isEqualTo(new SingleField("parentFullName", "Stone, Lily-Mae", null));
    }
    @Test
    public void shouldSelectFoodAssistanceCheckboxIfSnapIsSelected(){
        submission = new SubmissionTestBuilder()
            .with("unearnedIncomePrograms[]", List.of("SNAP")).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("unearnedIncomePrograms-snap")).isEqualTo(new SingleField("unearnedIncomePrograms-snap", "true", null));
    }
    @Test
    public void shouldSelectHomelessShelterCheckboxIfHomelessShelterIsSelected(){
        submission = new SubmissionTestBuilder()
            .with("unearnedIncomePrograms[]", List.of("HOMELESS_SHELTER_OR_PREVENTION_PROGRAMS")).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("unearnedIncomePrograms-homeless-shelters")).isEqualTo(new SingleField("unearnedIncomePrograms-homeless-shelters", "true", null));
    }
    @Test
    public void shouldSelectCashAssistanceCheckboxIfCashAssistanceIsSelected(){
        submission = new SubmissionTestBuilder()
            .with("unearnedIncomePrograms[]", List.of("CASH_ASSISTANCE")).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("unearnedIncomePrograms-tanf")).isEqualTo(new SingleField("unearnedIncomePrograms-tanf", "true", null));
    }
    @Test
    public void shouldSelectHousingVouchersCheckboxIfHousingVouchersIsSelected(){
        submission = new SubmissionTestBuilder()
            .with("unearnedIncomePrograms[]", List.of("HOUSING_VOUCHERS")).build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("unearnedIncomePrograms-homeless-shelters")).isEqualTo(new SingleField("unearnedIncomePrograms-homeless-shelters", "true", null));
    }
}
