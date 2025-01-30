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
    private final static String APPLICANT_EMPLOYER_INPUT_NAME = "activitiesJobStart";
    private final static String PARTNER_EMPLOYER_INPUT_NAME = "activitiesPartnerJobStart";

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        var employerStartDateInputNames = List.of(APPLICANT_EMPLOYER_INPUT_NAME, PARTNER_EMPLOYER_INPUT_NAME);
        for (var employerStartDateInputName: employerStartDateInputNames){
            int iteration = 1;
            String subflowName = employerStartDateInputName.equals(APPLICANT_EMPLOYER_INPUT_NAME) ? "jobs" : "partnerJobs";
            var jobs = ((List<Map<String, Object>>) submission.getInputData().getOrDefault(subflowName, emptyList())).stream().toList();
            for (var job : jobs) {
                var jobStart = getDateInputWithDayOptionalFromSubflow((HashMap<String, Object>) job, employerStartDateInputName);
                if(!jobStart.isEmpty()){
                    results.put(employerStartDateInputName + "_" +iteration,
                        new SingleField(employerStartDateInputName, jobStart, iteration));
                }
                iteration++;
            }
        }
        return results;
    }
}