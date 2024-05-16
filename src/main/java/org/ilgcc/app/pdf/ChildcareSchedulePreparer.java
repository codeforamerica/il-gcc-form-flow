package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import org.ilgcc.app.utils.ActivitySchedules.HourlySchedule;
import org.ilgcc.app.utils.ActivitySchedules.LocalTimeRange;
import org.ilgcc.app.utils.DayOfWeekOption;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.ilgcc.app.utils.SubmissionUtilities.getHourlySchedule;
import static org.ilgcc.app.utils.SubmissionUtilities.putSingleFieldResult;

@Component
public class ChildcareSchedulePreparer implements SubmissionFieldPreparer {
    private final DateTimeFormatter CLOCK_TIME_OF_AM_PM = DateTimeFormatter.ofPattern("hh:mm");
    private final DateTimeFormatter AM_PM_OF_DAY = DateTimeFormatter.ofPattern("a");

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        int iteration = 0;

        for (var child : SubmissionUtilities.getChildrenNeedingAssistance(submission)) {
            Optional<HourlySchedule> careSchedule =
                    getHourlySchedule(
                            child,
                            "childcare",
                            "childcareWeeklySchedule[]");
            if (careSchedule.isEmpty()) {
                continue;
            }
            iteration++;

            Map<DayOfWeekOption, LocalTimeRange> dailyScheduleMap = careSchedule.get().toDailyScheduleMap();
            for (var scheduleEntry : dailyScheduleMap.entrySet()) {
                DayOfWeekOption day = scheduleEntry.getKey();
                LocalTimeRange schedule = scheduleEntry.getValue();
                String fieldPrefix = "childCareSchedule" + day.name();
                putSingleFieldResult(
                        results,
                        fieldPrefix + "Start",
                        schedule.startTime().format(CLOCK_TIME_OF_AM_PM),
                        iteration);
                putSingleFieldResult(
                        results,
                        fieldPrefix + "StartAmPm",
                        schedule.startTime().format(AM_PM_OF_DAY),
                        iteration);
                putSingleFieldResult(
                        results,
                        fieldPrefix + "End",
                        schedule.endTime().format(CLOCK_TIME_OF_AM_PM),
                        iteration);
                putSingleFieldResult(
                        results,
                        fieldPrefix + "EndAmPm",
                        schedule.endTime().format(AM_PM_OF_DAY),
                        iteration);
            }
        }
        return results;
    }
}
