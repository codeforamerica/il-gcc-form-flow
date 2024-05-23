package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.ActivitySchedules.HourlySchedule;
import org.ilgcc.app.utils.ActivitySchedules.LocalTimeRange;
import org.ilgcc.app.utils.DayOfWeekOption;
import org.springframework.stereotype.Component;

@Component
public class ApplicantEducationSchedulePreparer implements SubmissionFieldPreparer {
    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        Optional<HourlySchedule> educationSchedule = SchedulePreparer.getHourlySchedule(submission, "activitiesClass", "weeklySchedule[]");
        if (educationSchedule.isEmpty()) {
            return results;
        }

        Map<DayOfWeekOption, LocalTimeRange> dailyScheduleMap = educationSchedule.get().toDailyScheduleMap();
        for (var scheduleEntry : dailyScheduleMap.entrySet()) {
            DayOfWeekOption day = scheduleEntry.getKey();
            LocalTimeRange schedule = scheduleEntry.getValue();
            results.putAll(SchedulePreparer.createSubmissionFieldsFromSchedule(schedule, day, "applicantEducationSchedule"));
        }
        return results;
    }
}