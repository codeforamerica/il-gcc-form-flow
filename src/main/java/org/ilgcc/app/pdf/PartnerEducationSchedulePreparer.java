package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.Map;
import org.ilgcc.app.utils.SchedulePreparerUtility;
import org.springframework.stereotype.Component;

@Component
public class PartnerEducationSchedulePreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        Map<String, String> careSchedule =
                SchedulePreparerUtility.hourlyScheduleKeys(
                        submission.getInputData(),
                        "partnerClass",
                        "partnerClassWeeklySchedule[]");

        results.putAll(
                SchedulePreparerUtility.createSubmissionFieldsFromDay(submission.getInputData(), careSchedule,
                        "partnerClass", "partnerEducationSchedule"));


        return results;
    }
}