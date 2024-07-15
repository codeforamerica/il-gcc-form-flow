package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.PreparerUtilities;
import org.springframework.stereotype.Component;

import static org.ilgcc.app.utils.PreparerUtilities.flowIteratorPreparer;
import static org.ilgcc.app.utils.SubmissionUtilities.getDateInput;
import static org.ilgcc.app.utils.SubmissionUtilities.formatToStringFromLocalDate;
import static org.ilgcc.app.utils.SubmissionUtilities.selectedYes;

@Component
public class ParentPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        var inputData = submission.getInputData();

        //active duty military
        String parentIsActiveDutyMilitary = (String) inputData.getOrDefault("parentIsServing", "");
        if (!parentIsActiveDutyMilitary.isBlank()) {
            results.put("parentIsServing", new SingleField("parentIsServing", selectedYes(parentIsActiveDutyMilitary), null));
        }

        //applicant is reserve or national guard
        String parentIsReserveOrNationalGuard = (String) inputData.getOrDefault("parentInMilitaryReserveOrNationalGuard", "");
        if (!parentIsReserveOrNationalGuard.isBlank()) {
            results.put("parentInMilitaryReserveOrNationalGuard",
                new SingleField("parentInMilitaryReserveOrNationalGuard", selectedYes(parentIsReserveOrNationalGuard), null));

        }

        boolean experiencingHomelessness = inputData.getOrDefault("parentHomeExperiencingHomelessness[]", "no").equals(
            List.of("yes"));
        results.put("parentExperiencingHomelessness",
            new SingleField("parentExperiencingHomelessness", Boolean.toString(experiencingHomelessness), null));

        var applicantSchoolStartDate = formatToStringFromLocalDate(getDateInput(submission, "activitiesProgramStart"));
        results.put("applicantSchoolStartDate",
            new SingleField("applicantSchoolStartDate", applicantSchoolStartDate, null));

        var applicantSchoolEndDate =  formatToStringFromLocalDate(getDateInput(submission, "activitiesProgramEnd"));
        results.put("applicantSchoolEndDate",
            new SingleField("applicantSchoolEndDate", applicantSchoolEndDate, null));

        var jobsCompanyFields = List.of("companyName", "employerStreetAddress","employerCity", "employerState", "employerZipCode", "employerPhoneNumber");
        Map jobsData = flowIteratorPreparer(submission, "jobs", jobsCompanyFields);

        if(!jobsData.isEmpty()){
            results.putAll(jobsData);
        }

        String educationTypePrefix = "APPLICANT";
        String educationTypeField = PreparerUtilities.getEducationTypeFieldValue(
            (String) submission.getInputData().getOrDefault("educationType", ""), educationTypePrefix);
        results.put("parentEducation",
            new SingleField("parentEducation", educationTypeField, null));

        List<String> parentGender = (List<String>) inputData.get("parentGender[]");
       if (parentGender != null && !parentGender.contains("NONE")) {
           if (parentGender.contains("MALE")) {
               results.put("parentGenderMale", new SingleField("parentGenderMale", "Yes", null));
           }
           if (parentGender.contains("FEMALE")) {
               results.put("parentGenderFemale", new SingleField("parentGenderFemale", "Yes", null));
           }
           if (parentGender.contains("TRANSGENDER") && parentGender.contains("NONBINARY")) {
               results.put("parentGenderTNB", new SingleField("parentGenderTNB", "Transgender, Nonbinary", null));

           } else if (parentGender.contains("TRANSGENDER")) {
               results.put("parentGenderTNB", new SingleField("parentGenderTNB", "Transgender", null));
           } else if (parentGender.contains("NONBINARY")){
               results.put("parentGenderTNB", new SingleField("parentGenderTNB", "Nonbinary", null));
           }

       }

        return results;
    }


}