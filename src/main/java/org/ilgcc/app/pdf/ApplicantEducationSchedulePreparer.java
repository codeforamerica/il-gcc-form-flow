package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.Map;
import org.ilgcc.app.utils.SchedulePreparerUtility;
import org.ilgcc.app.utils.enums.CommuteTimeType;
import org.ilgcc.app.utils.enums.TimeSpan;
import org.springframework.stereotype.Component;

@Component
public class ApplicantEducationSchedulePreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        Map<String, String> careSchedule =
                SchedulePreparerUtility.hourlyScheduleKeys(
                        submission.getInputData(),
                        "activitiesClass",
                        "weeklySchedule[]");

        results.putAll(
                SchedulePreparerUtility.createSubmissionFieldsFromDay(submission.getInputData(), careSchedule, "activitiesClass",
                        "applicantEducationSchedule"));

        String commuteTimeKey = (String) submission.getInputData().getOrDefault("activitiesEdCommuteTime", "");
        if (!commuteTimeKey.isBlank()) {
            TimeSpan commuteTimeValue = CommuteTimeType.getTimeSpanByName(commuteTimeKey);
            results.put("applicantSchoolTravelTimeHours",
                    new SingleField("applicantSchoolTravelTimeHours", commuteTimeValue.getPaddedHours(), null));
            results.put("applicantSchoolTravelTimeMins",
                    new SingleField("applicantSchoolTravelTimeMins", commuteTimeValue.getMinutes(), null));
        }

        return results;
    }
}
