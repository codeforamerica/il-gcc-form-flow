package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import org.ilgcc.app.utils.ActivitySchedules.HourlySchedule;
import org.ilgcc.app.utils.ActivitySchedules.LocalTimeRange;
import org.ilgcc.app.utils.DayOfWeekOption;
import org.ilgcc.app.utils.SchedulePreparerUtility;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ChildcareSchedulePreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        int iteration = 1;

        for (var child : SubmissionUtilities.getChildrenNeedingAssistance(submission)) {

            Map<String, String> careSchedule =
                    SchedulePreparerUtility.hourlyScheduleKeys(
                            child,
                            "childcare",
                            "childcareWeeklySchedule[]");

            results.putAll(
                    SchedulePreparerUtility.createSubmissionFieldsFromDay(child, careSchedule, "childcare", "childCareSchedule",
                            iteration));

            iteration++;
        }
        return results;
    }
}
