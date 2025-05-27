package org.ilgcc.app.pdf.helpers;

import static java.util.Collections.emptyList;

import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ProviderHouseholdMemberPreparerHelper extends InputDataPreparerHelper {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Map<String, Object> providerInputData) {
        Map<String, SubmissionField> results = new HashMap<>();

        int iteration = 1;

        var providerHouseholdMembers = (List<Map<String, Object>>) providerInputData.getOrDefault("providerHouseholdMembers",
                emptyList());
        for (var householdMember : providerHouseholdMembers) {
            results.put("providerHouseholdMemberFirstName_" + iteration,
                    new SingleField("providerHouseholdMemberFirstName",
                            (String) householdMember.getOrDefault("providerHouseholdMemberFirstName", ""), iteration));
            results.put("providerHouseholdMemberLastName_" + iteration,
                    new SingleField("providerHouseholdMemberLastName",
                            (String) householdMember.getOrDefault("providerHouseholdMemberLastName", ""), iteration));
            results.put("providerHouseholdMemberDateOfBirth_" + iteration,
                    new SingleField("providerHouseholdMemberDateOfBirth", formatHouseholdMemberDateOfBirth(householdMember),
                            iteration));
            results.put("providerHouseholdMemberRelationship_" + iteration,
                    new SingleField("providerHouseholdMemberRelationship",
                            (String) householdMember.getOrDefault("providerHouseholdMemberRelationship", ""), iteration));
            results.put("providerHouseholdMemberSSN_" + iteration,
                    new SingleField("providerHouseholdMemberSSN",
                            (String) householdMember.getOrDefault("providerHouseholdMemberSSN", ""), iteration));
            iteration++;
        }
        return results;
    }

    private String formatHouseholdMemberDateOfBirth(Map<String, Object> providerHouseholdMember) {
        return String.format("%s/%s/%s",
                providerHouseholdMember.getOrDefault("providerHouseholdMemberDateOfBirthMonth", ""),
                providerHouseholdMember.getOrDefault("providerHouseholdMemberDateOfBirthDay", ""),
                providerHouseholdMember.getOrDefault("providerHouseholdMemberDateOfBirthYear", ""));
    }

}
