package org.ilgcc.app.pdf;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SubmissionUtilities.getDateInputWithDayOptionalFromSubflow;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class EmployerStartDatePreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        int iteration = 1;
        var jobs = ((List<Map<String, Object>>) submission.getInputData().getOrDefault("jobs", emptyList())).stream().toList();
        for (var job : jobs) {
            var activitiesJobStart = getDateInputWithDayOptionalFromSubflow((HashMap<String, Object>) job, "activitiesJobStart");
            if(!activitiesJobStart.isEmpty()){
                results.put("activitiesJobStart" + "_" +iteration,
                    new SingleField("activitiesJobStart", activitiesJobStart, iteration));
            }
            iteration++;
        }

        int partnerIteration = 1;
        var partnerJobs = ((List<Map<String, Object>>) submission.getInputData().getOrDefault("partnerJobs", emptyList())).stream().toList();
        for (var partnerJob : partnerJobs) {
            var activitiesJobStart = getDateInputWithDayOptionalFromSubflow((HashMap<String, Object>) partnerJob, "activitiesPartnerJobStart");
            if(!activitiesJobStart.isEmpty()){
                results.put("activitiesPartnerJobStart" + "_" +partnerIteration,
                    new SingleField("activitiesPartnerJobStart", activitiesJobStart, partnerIteration));
            }
            partnerIteration++;
        }
        return results;
    }
}