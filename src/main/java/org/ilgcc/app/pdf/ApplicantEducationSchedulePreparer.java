package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import org.ilgcc.app.utils.ActivitySchedules.HourlySchedule;
import org.ilgcc.app.utils.ActivitySchedules.LocalTimeRange;
import org.ilgcc.app.utils.DayOfWeekOption;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.ilgcc.app.utils.SubmissionUtilities.*;

@Component
public class ApplicantEducationSchedulePreparer implements SubmissionFieldPreparer {
    private final DateTimeFormatter CLOCK_TIME_OF_AM_PM = DateTimeFormatter.ofPattern("hh:mm");
    private final DateTimeFormatter AM_PM_OF_DAY = DateTimeFormatter.ofPattern("a");

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Optional<HourlySchedule> activitiesClassSchedule =
                getHourlySchedule(submission, "activitiesClass", "weeklySchedule[]");
        var results = new HashMap<String, SubmissionField>();
        if (activitiesClassSchedule.isEmpty()) {
            return results;
        }

        Map<DayOfWeekOption, LocalTimeRange> dailyScheduleMap = activitiesClassSchedule.get().toDailyScheduleMap();
        for (var scheduleEntry : dailyScheduleMap.entrySet()) {
            DayOfWeekOption day = scheduleEntry.getKey();
            LocalTimeRange schedule = scheduleEntry.getValue();
            String fieldPrefix = "applicantEducationSchedule" + day.name();
            putSingleFieldResult(
                    results,
                    fieldPrefix + "Start",
                    schedule.startTime().format(CLOCK_TIME_OF_AM_PM));
            putSingleFieldResult(
                    results,
                    fieldPrefix + "StartAmPm",
                    schedule.startTime().format(AM_PM_OF_DAY));
            putSingleFieldResult(
                    results,
                    fieldPrefix + "End",
                    schedule.endTime().format(CLOCK_TIME_OF_AM_PM));
            putSingleFieldResult(
                    results,
                    fieldPrefix + "EndAmPm",
                    schedule.endTime().format(AM_PM_OF_DAY));
        }
        return results;
    }
}