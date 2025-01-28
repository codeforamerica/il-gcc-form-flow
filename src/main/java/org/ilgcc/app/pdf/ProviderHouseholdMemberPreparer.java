package org.ilgcc.app.pdf;

import static java.util.Collections.emptyList;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ProviderHouseholdMemberPreparer extends ProviderSubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission familySubmission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();
        Optional<Submission> providerSubmission = getProviderSubmission(familySubmission);
        if (providerSubmission.isEmpty()) {
            return results;
        }

        var providerInputData = providerSubmission.get().getInputData();
        int iteration = 1;

        var providerHouseholdMembers = (List<Map<String, Object>>) providerInputData.getOrDefault("providerHouseholdMembers", emptyList());
        for (var householdMember : providerHouseholdMembers) {
            results.put("providerHouseholdMemberFirstName_" + iteration,
                    new SingleField("providerHouseholdMemberFirstName", (String) householdMember.getOrDefault("providerHouseholdMemberFirstName", ""), iteration));
            results.put("providerHouseholdMemberLastName_" + iteration,
                    new SingleField("providerHouseholdMemberLastName", (String) householdMember.getOrDefault("providerHouseholdMemberLastName", ""), iteration));
            results.put("providerHouseholdMemberDateOfBirth_" + iteration,
                    new SingleField("providerHouseholdMemberDateOfBirth", formatHouseholdMemberDateOfBirth(householdMember), iteration));
            results.put("providerHouseholdMemberRelationship_" + iteration,
                new SingleField("providerHouseholdMemberRelationship", (String) householdMember.getOrDefault("providerHouseholdMemberRelationship", ""), iteration));
            results.put("providerHouseholdMemberSSN_" + iteration,
                new SingleField("providerHouseholdMemberSSN", (String) householdMember.getOrDefault("providerHouseholdMemberSSN", ""), iteration));
            iteration++;
        }
        return results;
    }

    private String formatHouseholdMemberDateOfBirth(Map<String, Object> providerHouseholdMember) {
        return String.format("%s/%s/%s",
                providerHouseholdMember.getOrDefault("providerHouseholdMemberDateOfBirthMonth",""),
                providerHouseholdMember.getOrDefault("providerHouseholdMemberDateOfBirthDay", ""),
                providerHouseholdMember.getOrDefault("providerHouseholdMemberDateOfBirthYear", ""));
    }

}
