package org.ilgcc.app.pdf;

import static org.ilgcc.app.utils.PreparerUtilities.formatYesNo;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.GenderOption;
import org.ilgcc.app.utils.RaceEthnicityOption;
import org.ilgcc.app.utils.SchedulePreparerUtility;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NeedChildcareChildrenPreparer implements SubmissionFieldPreparer {

    private boolean enableMultipleProviders;

    public NeedChildcareChildrenPreparer(
            @Value("${il-gcc.enable-multiple-providers}") boolean enableMultipleProviders) {
        this.enableMultipleProviders = enableMultipleProviders;
    }

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        int iteration = 1;
        for (var child : SubmissionUtilities.firstFourChildrenNeedingAssistance(submission)) {
            results.put("familySectionChildFirstName_" + iteration,
                    new SingleField("familySectionChildFirstName", (String) child.getOrDefault("childFirstName", ""), iteration));
            results.put("familySectionChildLastName_" + iteration,
                    new SingleField("familySectionChildLastName", (String) child.getOrDefault("childLastName", ""), iteration));
            results.put("childDateOfBirth_" + iteration,
                    new SingleField("childDateOfBirth", formatChildDateOfBirth(child), iteration));
            results.put("childRaceEthnicity_" + iteration, new SingleField("childRaceEthnicity",
                    formatChildRaceEthnicity((List) child.getOrDefault("childRaceEthnicity[]", List.of())), iteration));
            results.put("childGender_" + iteration,
                    new SingleField("childGender", formatChildGender((List) child.getOrDefault("childGender[]", List.of())),
                            iteration));
            results.put("childSpecialNeeds_" + iteration,
                    new SingleField("childSpecialNeeds", formatYesNo((String) child.getOrDefault("childHasDisability", "")),
                            iteration));
            results.put("childUSCitizen_" + iteration,
                    new SingleField("childUSCitizen", formatYesNo((String) child.getOrDefault("childIsUsCitizen", "")),
                            iteration));
            results.put("familySectionChildRelationship_" + iteration,
                    new SingleField("familySectionChildRelationship", (String) child.get("childRelationship"), iteration));

            if(!enableMultipleProviders || SubmissionUtilities.isPreMultiProviderApplicationWithSingleProvider(submission)){
                results.putAll(prepareChildCareSchedule(child, iteration));
            }

            iteration++;
        }
        return results;
    }

    public Map<String, SubmissionField> prepareChildCareSchedule(Map<String, Object> child, int iteration){
        var fields = new HashMap<String, SubmissionField>();
        fields.put("childFirstName_" + iteration,
                new SingleField("childFirstName", (String) child.getOrDefault("childFirstName", ""), iteration));
        fields.put("childLastName_" + iteration,
                new SingleField("childLastName", (String) child.getOrDefault("childLastName", ""), iteration));
        fields.put("childRelationship_" + iteration,
                new SingleField("childRelationship", (String) child.get("childRelationship"), iteration));
        fields.put("childCareChildInSchool_" + iteration,
                new SingleField("childCareChildInSchool", (String) child.getOrDefault("childAttendsOtherEd", ""), iteration));
        fields.put("childOtherEdHoursDescription_" + iteration,
                new SingleField("childOtherEdHoursDescription", (String) child.getOrDefault("childOtherEdHoursDescription", ""), iteration));

        Map<String, String> careSchedule =
                SchedulePreparerUtility.hourlyScheduleKeys(
                        child,
                        "childcare",
                        "childcareWeeklySchedule[]");

        fields.putAll(
                SchedulePreparerUtility.createSubmissionFieldsFromDay(child, careSchedule, "childcare", "childCareSchedule",
                        iteration));
        return fields;
    }

    private String formatChildDateOfBirth(Map<String, Object> child) {
        return String.format("%s/%s/%s",
                child.get("childDateOfBirthMonth"),
                child.get("childDateOfBirthDay"),
                child.get("childDateOfBirthYear"));
    }

    private String formatChildRaceEthnicity(List raceEthnicity) {
        if (raceEthnicity.isEmpty()) {
            return "";
        } else if (raceEthnicity.equals(List.of("NONE"))) {
            return "X";
        } else {
            return String.join(", ",
                    raceEthnicity.stream().map(name -> RaceEthnicityOption.getPdfValueByName(String.valueOf(name))).distinct()
                            .sorted().toList());
        }
    }

    private String formatChildGender(List gender) {
        if (gender.isEmpty() || gender.equals(List.of("NONE"))) {
            return "";
        } else {
            return String.join(",",
                    gender.stream().map(name -> GenderOption.getPdfValueByName(String.valueOf(name))).distinct().sorted()
                            .toList());
        }
    }
}
