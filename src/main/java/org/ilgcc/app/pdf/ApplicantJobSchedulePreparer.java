package org.ilgcc.app.pdf;

import static java.util.Collections.emptyList;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
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
public class ApplicantJobSchedulePreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        int iteration = 1;

        ArrayList<Map> jobs = (ArrayList<Map>) submission.getInputData().getOrDefault("jobs", emptyList());

        for (var job : jobs) {
            Optional<HourlySchedule> workSchedule =
                SchedulePreparer.getHourlySchedule(
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
                results.putAll(
                    SchedulePreparer.createSubmissionFieldsFromSchedule(schedule, day, "applicantEmployerSchedule",
                        iteration));
            }
            iteration++;

        }

        return results;
    }
}