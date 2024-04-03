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
public class NeedChildcareForChildren implements SubmissionFieldPreparer {

  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
    var results = new HashMap<String, SubmissionField>();
    int iteration = 1;

    var children = ((List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList())).stream()
        .filter(child -> child.getOrDefault("needFinancialAssistanceForChild", "No").equals("Yes"))
        .toList();
    for (var child : children) {
      results.put(getUniqueKey(), new SingleField("childFirstName", (String) child.get("childFirstName"), iteration));
      results.put(getUniqueKey(), new SingleField("childLastName", (String) child.get("childLastName"), iteration));
      results.put(getUniqueKey(), new SingleField("childDateOfBirth", formatChildDateOfBirth(child), iteration));
      results.put(getUniqueKey(), new SingleField("childSpecialNeeds", formatYesNo((String) child.get("childHasDisability")), iteration));
      results.put(getUniqueKey(), new SingleField("childUSCitizen", formatYesNo((String) child.get("childIsUsCitizen")), iteration));
      iteration++;
    }

    return results;
  }

  private String getUniqueKey() {
    return getClass().getName() + new Random();
  }

  private String formatChildDateOfBirth(Map<String, Object> child) {
    return String.format("%s/%s/%s",
        child.get("childDateOfBirthMonth"),
        child.get("childDateOfBirthDay"),
        child.get("childDateOfBirthYear"));
  }

  private String formatYesNo(String value) {
    return switch (value) {
      case "Yes" -> "true";
      case "No" -> "false";
      default -> "";
    };
  }

}
