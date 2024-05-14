package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class FormatSubmittedAtDate implements Action {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a");

    @Override
    public void run(Submission submission) {
        OffsetDateTime submittedAt = submission.getSubmittedAt();

        if (submittedAt != null) {
            String formattedSubmittedAtDate = submittedAt.format(DATE_FORMAT);
            String formattedSubmittedAtTime = submittedAt.format(TIME_FORMAT);
            submission.getInputData().put("formattedSubmittedAtDate", formattedSubmittedAtDate);
            submission.getInputData().put("formattedSubmittedAtTime", formattedSubmittedAtTime);
        }
    }
}
