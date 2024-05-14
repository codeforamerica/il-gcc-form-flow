package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class FormatSubmittedAtDateTest {
    private final FormatSubmittedAtDate action = new FormatSubmittedAtDate();

    @Test
    public void shouldShowDateTimeInCentralTime() {
        String testTime = "2024-05-13T22:05:06+00:00";
        OffsetDateTime parsedTestTime = OffsetDateTime.parse(testTime);

        ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");
        OffsetDateTime chicagoTime = parsedTestTime.atZoneSameInstant(chicagoTimeZone).toOffsetDateTime();
        chicagoTime.format(DateTimeFormatter.ofPattern("hh:mm A"));

        Submission submission = Submission.builder()
                .urlParams(new HashMap<>()).inputData(new HashMap<>()).submittedAt(parsedTestTime).build();

        action.run(submission);

        String expectedDate = "05/13/2024";
        String expectedTime = "5:05 PM";
        assertThat(submission.getInputData().get("formattedSubmittedAtDate")).isEqualTo(expectedDate);
        assertThat(submission.getInputData().get("formattedSubmittedAtTime")).isEqualTo(expectedTime);
    }
}
