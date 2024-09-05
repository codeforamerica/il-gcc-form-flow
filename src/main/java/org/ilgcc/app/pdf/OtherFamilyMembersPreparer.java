package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.Collections.emptyList;

@Component
public class OtherFamilyMembersPreparer implements SubmissionFieldPreparer {

  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
    var results = new HashMap<String, SubmissionField>();
    int iteration = 1;

    var children = ((List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList())).stream()
        .filter(child -> child.getOrDefault("needFinancialAssistanceForChild", "No").equals("No"))
        .toList();
    for (var child : children) {
      results.put("familyMemberFirstName_" + iteration, new SingleField("familyMemberFirstName", (String) child.get("childFirstName"), iteration));
      results.put("familyMemberLastName_" + iteration, new SingleField("familyMemberLastName", (String) child.get("childLastName"), iteration));
      results.put("familyMemberDateOfBirth_" + iteration, new SingleField("familyMemberDateOfBirth", formatChildDateOfBirth(child), iteration));
      results.put("familyMemberRelationship_" + iteration, new SingleField("familyMemberRelationship", (String) child.get("childRelationship"), iteration));
      iteration++;
    }

    var adultDependents = (List<Map<String, Object>>) submission.getInputData().getOrDefault("adultDependents", emptyList());
    for (var adult : adultDependents) {
      results.put("familyMemberFirstName_" + iteration, new SingleField("familyMemberFirstName", (String) adult.get("adultDependentFirstName"), iteration));
      results.put("familyMemberLastName_" + iteration, new SingleField("familyMemberLastName", (String) adult.get("adultDependentLastName"), iteration));
      results.put("familyMemberDateOfBirth_" + iteration, new SingleField("familyMemberDateOfBirth", formatAdultDependentDateOfBirth(adult), iteration));
      iteration++;
    }

    return results;
  }

  private String formatChildDateOfBirth(Map<String, Object> child) {
    return String.format("%s/%s/%s",
        child.get("childDateOfBirthMonth"),
        child.get("childDateOfBirthDay"),
        child.get("childDateOfBirthYear"));
  }

  private String formatAdultDependentDateOfBirth(Map<String, Object> adult) {
    var year = (String) adult.getOrDefault("adultDependentBirthdateYear", "");
    if (year.isBlank()) {
      return "";
    }

    return String.format("%s/%s/%s",
        adult.get("adultDependentBirthdateMonth"),
        adult.get("adultDependentBirthdateDay"),
        year);
  }
}
