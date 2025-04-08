package org.ilgcc.app.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class ProviderSubmissionUtilitiesTest {

    @Test
    void formatChildNamesAsCommaSeparatedList() {
        Submission singleChildName = new SubmissionTestBuilder().withChild("John", "Doe", "true").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeparatedList(singleChildName, "and")).isEqualTo("John Doe");

        Submission twoChildren = new SubmissionTestBuilder().withChild("John", "Doe", "true").withChild("Jane", "Doe", "true")
                .build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeparatedList(twoChildren, "and")).isEqualTo(
                "John Doe and Jane Doe");

        Submission threeChildren = new SubmissionTestBuilder().withChild("John", "Doe", "true").withChild("Jane", "Doe", "true")
                .withChild("June", "Doe", "true").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeparatedList(threeChildren, "and")).isEqualTo(
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

    @Test
    void threeBusinessDaysBeforeDate_noHoliday() {
        // Friday current date, Tuesday rollback
        OffsetDateTime dateWeCareAbout = OffsetDateTime.parse("2025-09-12T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-09T15:56:54+00:00"));

        // Thursday current date, Monday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-11T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-08T15:56:54+00:00"));

        // Wednesday current date, Friday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-10T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-05T15:56:54+00:00"));

        // Tuesday current date, Thursday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-09T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-04T15:56:54+00:00"));

        // Monday current date, Wednesday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-08T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-03T15:56:54+00:00"));

        // Sunday current date, Wednesday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-07T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-03T15:56:54+00:00"));

        // Saturday current date, Wednesday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-06T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-03T15:56:54+00:00"));
    }

    @Test
    void threeBusinessDaysBeforeDate_Holiday() {
        // Friday current date, Tuesday rollback
        OffsetDateTime dateWeCareAbout = OffsetDateTime.parse("2025-09-05T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-09-02T15:56:54+00:00"));

        // Thursday current date, Monday holiday, Friday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-04T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-08-29T15:56:54+00:00"));

        // Wednesday current date, Monday holiday, Thursday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-03T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-08-28T15:56:54+00:00"));

        // Tuesday current date, Monday holiday, Wednesday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-02T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-08-27T15:56:54+00:00"));

        // Monday current date, Monday holiday, Tuesday rollback
        dateWeCareAbout = OffsetDateTime.parse("2025-09-01T15:56:54+00:00");
        assertThat(ProviderSubmissionUtilities.threeBusinessDaysBeforeDate(dateWeCareAbout)).isEqualTo(
                OffsetDateTime.parse("2025-08-27T15:56:54+00:00"));

    }
}