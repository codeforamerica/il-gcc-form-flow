package org.ilgcc.app.pdf;

import static java.util.Collections.emptyList;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SubmissionField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.ActivitySchedules.HourlySchedule;
import org.ilgcc.app.utils.ActivitySchedules.LocalTimeRange;
import org.ilgcc.app.utils.DayOfWeekOption;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class ApplicantJobSchedulePreparer extends SchedulePreparer {
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        int iteration = 1;

        ArrayList<Map> jobs = (ArrayList<Map>) submission.getInputData().getOrDefault("jobs", emptyList());

        for (var job : jobs) {
            Optional<HourlySchedule> workSchedule =
                getHourlySchedule(
                    job,
                    "activitiesJob",
                    "activitiesJobWeeklySchedule[]");
            if (workSchedule.isEmpty()) {
                continue;
            }

            Map<DayOfWeekOption, LocalTimeRange> dailyScheduleMap = workSchedule.get().toDailyScheduleMap();
            for (var scheduleEntry : dailyScheduleMap.entrySet()) {
                DayOfWeekOption day = scheduleEntry.getKey();
                LocalTimeRange schedule = scheduleEntry.getValue();
                String fieldPrefix = "applicantEmployerSchedule" + day.name();
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
            iteration++;
        }
        return results;
    }
}