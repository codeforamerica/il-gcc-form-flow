package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.CheckboxField;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.utils.ChildcareReasonOption;
import org.springframework.stereotype.Component;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SubmissionUtilities.getDateInput;
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

        Boolean experiencingHomelessness = (Boolean) inputData.getOrDefault("parentHomeExperiencingHomelessness[]", "no").equals(
            List.of("yes"));
        results.put("parentExperiencingHomelessness",
            new SingleField("parentExperiencingHomelessness", experiencingHomelessness.toString(), null));

        List parentActivities = (List) inputData.getOrDefault("activitiesParentChildcareReason", emptyList());
        Boolean parentAttendingSchool = parentActivities.contains(ChildcareReasonOption.SCHOOL);
        if (parentAttendingSchool) {
            results.put("schoolName", new SingleField("schoolName", inputData.get("schoolName").toString(), null));
            results.put("streetAddress",
                new SingleField("streetAddress", inputData.getOrDefault("streetAddress", "").toString(), null));
            results.put("city", new SingleField("city", inputData.getOrDefault("city", "").toString(), null));
            results.put("state", new SingleField("state", inputData.getOrDefault("state", "").toString(), null));
            results.put("zipCode", new SingleField("zipCode", inputData.getOrDefault("zipCode", "").toString(), null));
            results.put("phoneNumber",
                new SingleField("phoneNumber", inputData.getOrDefault("phoneNumber", "").toString(), null));

            var applicantSchoolStartDate = generateLocalDate(submission,  "activitiesProgramStartYear", "activitiesProgramStartMonth", "activitiesProgramStartDay");
            results.put("applicantSchoolStartDate",
                new SingleField("applicantSchoolStartDate",applicantSchoolStartDate, null));

            var applicantSchoolEndDate = generateLocalDate(submission,  "activitiesProgramEndYear", "activitiesProgramEndMonth", "activitiesProgramEndDay");
            results.put("applicantSchoolEndDate",
                new SingleField("applicantSchoolEndDate",applicantSchoolEndDate, null));
        }

        return results;
    }

    private String generateLocalDate(Submission submission, String yearKey, String monthKey, String dayKey) {
        String day = (String) submission.getInputData().getOrDefault(dayKey, "");
        String month = (String) submission.getInputData().getOrDefault(monthKey, "");
        String year = (String) submission.getInputData().getOrDefault(yearKey, "");

        if(!day.isBlank() && !month.isBlank() && !year.isBlank()){
            int intYear = Integer.parseInt(year);
            int intMonth = Integer.parseInt(month);
            int intDay = Integer.parseInt(day);

            LocalDate date = LocalDate.of(intYear, intMonth, intDay);
            return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        }

        return "";
    }
}
