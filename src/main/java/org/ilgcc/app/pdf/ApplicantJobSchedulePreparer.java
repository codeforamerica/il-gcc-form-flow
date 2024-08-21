package org.ilgcc.app.pdf;

import static java.util.Collections.emptyList;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.ActivitySchedules.HourlySchedule;
import org.ilgcc.app.utils.ActivitySchedules.LocalTimeRange;
import org.ilgcc.app.utils.DayOfWeekOption;
import org.ilgcc.app.utils.SchedulePreparerUtility;
import org.ilgcc.app.utils.enums.CommuteTimeType;
import org.ilgcc.app.utils.enums.TimeSpan;
import org.springframework.stereotype.Component;

@Component
public class ApplicantJobSchedulePreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        int iteration = 1;

        List<Map> jobs = (List<Map>) submission.getInputData().getOrDefault("jobs", emptyList());

        for (var job : jobs) {
            Map<String, String> careSchedule =
                SchedulePreparerUtility.hourlyScheduleKeys(
                    (Map<String, Object>) job,
                    "activitiesJob",
                    "activitiesJobWeeklySchedule[]");

            for (var day : careSchedule.keySet()) {
                results.putAll(
                        SchedulePreparerUtility.createSubmissionFieldsFromDay(job, day, careSchedule.get(day), "activitiesJob", "applicantEmployerSchedule",
                                iteration));
            }

            String commuteTimeKey = (String) job.getOrDefault("activitiesJobCommuteTime", "");

            if(!commuteTimeKey.isBlank()){
                TimeSpan commuteTimeValue = CommuteTimeType.getTimeSpanByName(commuteTimeKey);
                results.put("applicantEmployerTravelTimeHours_"+iteration, new SingleField("applicantEmployerTravelTimeHours", commuteTimeValue.getHours(), iteration));
                results.put("applicantEmployerTravelTimeMins_"+iteration, new SingleField("applicantEmployerTravelTimeMins", commuteTimeValue.getMinutes(), iteration));
            }
            iteration++;

        }

        return results;
    }
}