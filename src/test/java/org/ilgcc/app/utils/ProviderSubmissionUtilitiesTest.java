package org.ilgcc.app.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class ProviderSubmissionUtilitiesTest {

    @Test
    void formatChildNamesAsCommaSeperatedList() {
        Submission singleChildName = new SubmissionTestBuilder().withChild("John", "Doe", "Yes").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeperatedList(singleChildName)).isEqualTo("John Doe");

        Submission twoChildren = new SubmissionTestBuilder().withChild("John", "Doe", "Yes").withChild("Jane", "Doe", "Yes")
                .build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeperatedList(twoChildren)).isEqualTo(
                "John Doe and Jane Doe");

        Submission threeChildren = new SubmissionTestBuilder().withChild("John", "Doe", "Yes").withChild("Jane", "Doe", "Yes")
                .withChild("June", "Doe", "Yes").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeperatedList(threeChildren)).isEqualTo(
                "John Doe, Jane Doe and June Doe");
    }

    @Test
    void threeBusinessDaysFromSubmittedAtDate_noHoliday() {
        ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");

        // Monday submission, Thursday Expiration
        OffsetDateTime submittedAt = OffsetDateTime.parse("2024-12-02T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 5, 9, 56, 54).atZone(chicagoTimeZone));

        // Tuesday submission, Friday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-03T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 6, 9, 56, 54).atZone(chicagoTimeZone));

        // Wednesday submission, Monday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-04T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 9, 9, 56, 54).atZone(chicagoTimeZone));

        // Thursday submission, Tuesday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-05T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 10, 9, 56, 54).atZone(chicagoTimeZone));

        // Friday submission, Wednesday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-06T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 11, 9, 56, 54).atZone(chicagoTimeZone));

        // Saturday submission, Wednesday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-07T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 11, 9, 56, 54).atZone(chicagoTimeZone));

        // Sunday submission, Wednesday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-08T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 11, 9, 56, 54).atZone(chicagoTimeZone));
    }

    @Test
    void threeBusinessDaysFromSubmittedAtDate_WithHoliday() {

        ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");

        // Monday submission, Wednesday Holiday, Friday Expiration
        OffsetDateTime submittedAt = OffsetDateTime.parse("2024-12-23T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 27, 9, 56, 54).atZone(chicagoTimeZone));

        // Tuesday submission, Wednesday Holiday, Monday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-24T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 30, 9, 56, 54).atZone(chicagoTimeZone));

        // Wednesday submission, Wednesday Holiday, Monday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-25T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 30, 9, 56, 54).atZone(chicagoTimeZone));

        // Thursday submission, Tuesday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-26T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2024, 12, 31, 9, 56, 54).atZone(chicagoTimeZone));

        // Friday submission, Wednesday Holiday, Thursday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-27T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2025, 1, 2, 9, 56, 54).atZone(chicagoTimeZone));

        // Saturday submission, Wednesday Holiday, Thursday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-28T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2025, 1, 2, 9, 56, 54).atZone(chicagoTimeZone));

        // Sunday submission, Wednesday Holiday, Thursday Expiration
        submittedAt = OffsetDateTime.parse("2024-12-29T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysFromSubmittedAtDate(submittedAt)).isEqualTo(
                LocalDateTime.of(2025, 1, 2, 9, 56, 54).atZone(chicagoTimeZone));
    }
}