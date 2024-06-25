package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.time.LocalDate;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import static org.ilgcc.app.utils.PreparerUtilities.formatYesNo;

@Component
public class NeedChildcareForChildren implements SubmissionFieldPreparer {

  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
    var results = new HashMap<String, SubmissionField>();
    int iteration = 1;
    String earliestCCAPStart= "";
    for (var child : SubmissionUtilities.getChildrenNeedingAssistance(submission)) {
      results.put(getUniqueKey(), new SingleField("childFirstName", (String) child.getOrDefault("childFirstName", ""), iteration));
      results.put(getUniqueKey(), new SingleField("childLastName", (String) child.getOrDefault("childLastName", ""), iteration));
      results.put(getUniqueKey(), new SingleField("childDateOfBirth", formatChildDateOfBirth(child), iteration));
      results.put("childSpecialNeeds_" + iteration, new SingleField("childSpecialNeeds", formatYesNo((String) child.getOrDefault("childHasDisability", "")), iteration));
      results.put("childUSCitizen_" + iteration, new SingleField("childUSCitizen", formatYesNo((String) child.getOrDefault("childIsUsCitizen", "")), iteration));
      results.put("childCareChildInSchool_" + iteration, new SingleField("childCareChildInSchool", (String) child.getOrDefault("childAttendsOtherEd", ""), iteration));
      earliestCCAPStart = getEarliestCCAPStartDate(earliestCCAPStart, child.getOrDefault("ccapStartDate", ""));
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

  private String getEarliestCCAPStartDate(String earliestCCAPStartDate, String childCCAPStartDate){
    if(earliestCCAPStartDate.isBlank()){
      return childCCAPStartDate;
    }
    return childCCAPStartDate;
  }

}
