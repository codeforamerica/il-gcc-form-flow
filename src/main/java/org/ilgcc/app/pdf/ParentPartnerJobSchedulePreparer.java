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
public class ParentPartnerJobSchedulePreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        int iteration = 1;

        List<Map> jobs = (List<Map>) submission.getInputData().getOrDefault("partnerJobs", emptyList());
        results.put("partnerEmployerNumberOfJobs",
                new SingleField("partnerEmployerNumberOfJobs", String.valueOf(jobs.size()), null));

        for (var job : jobs) {
            Map<String, String> careSchedule =
                    SchedulePreparerUtility.hourlyScheduleKeys(
                            (Map<String, Object>) job,
                            "activitiesJob",
                            "activitiesJobWeeklySchedule[]");
            results.putAll(
                    SchedulePreparerUtility.createSubmissionFieldsFromDay(job, careSchedule, "activitiesJob",
                            "partnerEmployerSchedule",
                            iteration));

            String commuteTimeKey = (String) job.getOrDefault("activitiesJobCommuteTime", "");

            if (!commuteTimeKey.isBlank()) {
                TimeSpan commuteTimeValue = CommuteTimeType.getTimeSpanByName(commuteTimeKey);
                results.put("partnerEmployerTravelTimeHours_" + iteration,
                        new SingleField("partnerEmployerTravelTimeHours", commuteTimeValue.getPaddedHours(), iteration));
                results.put("partnerEmployerTravelTimeMins_" + iteration,
                        new SingleField("partnerEmployerTravelTimeMins", commuteTimeValue.getMinutes(), iteration));
            }
            String activitiesWorkVary = (String) job.getOrDefault("activitiesWorkVary", "false");
            if ("true".equalsIgnoreCase(activitiesWorkVary)){
                results.put("partnerEmployerWorkHoursVaryExplanation_" + iteration,
                        new SingleField("partnerEmployerWorkHoursVaryExplanation", "Work schedule varies",  iteration));
            }
            iteration++;

        }
        results.forEach((k,v)->System.out.println(k+"->"+v.getName()));
        return results;
    }
}
