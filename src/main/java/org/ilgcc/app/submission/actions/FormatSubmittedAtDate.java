package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class FormatSubmittedAtDate implements Action {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a");

    @Override
    public void run(Submission submission) {
        if (submission.getSubmittedAt() != null) {
            ZoneId chicagoTimeZone = ZoneId.of("America/Chicago");
            ZonedDateTime submittedAt = submission.getSubmittedAt().atZoneSameInstant(chicagoTimeZone);

            String formattedSubmittedAtDate = submittedAt.format(DATE_FORMAT);
            String formattedSubmittedAtTime = submittedAt.format(TIME_FORMAT);
            submission.getInputData().put("formattedSubmittedAtDate", formattedSubmittedAtDate);
            submission.getInputData().put("formattedSubmittedAtTime", formattedSubmittedAtTime);
        }
    }
}
