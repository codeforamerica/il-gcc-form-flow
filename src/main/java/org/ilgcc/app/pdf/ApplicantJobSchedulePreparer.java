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

            results.putAll(
                    SchedulePreparerUtility.createSubmissionFieldsFromDay(job, careSchedule, "activitiesJob",
                            "applicantEmployerSchedule",
                            iteration));

            String commuteTimeKey = (String) job.getOrDefault("activitiesJobCommuteTime", "");

            if (!commuteTimeKey.isBlank()) {
                TimeSpan commuteTimeValue = CommuteTimeType.getTimeSpanByName(commuteTimeKey);
                results.put("applicantEmployerTravelTimeHours_" + iteration,
                        new SingleField("applicantEmployerTravelTimeHours", commuteTimeValue.getPaddedHours(), iteration));
                results.put("applicantEmployerTravelTimeMins_" + iteration,
                        new SingleField("applicantEmployerTravelTimeMins", commuteTimeValue.getMinutes(), iteration));
            }
            iteration++;

        }

        return results;
    }
}
