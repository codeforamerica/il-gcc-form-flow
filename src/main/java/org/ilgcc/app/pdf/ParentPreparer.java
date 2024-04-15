package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.CheckboxField;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ParentPreparer implements SubmissionFieldPreparer {

  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
    var results= new HashMap<String, SubmissionField>();
    //active duty military
    String parentIsActiveDutyMilitary = (String) submission.getInputData().getOrDefault("parentIsServing", "");
    if (!parentIsActiveDutyMilitary.isBlank()){
      results.put("parentIsServing", new SingleField("parentIsServing", selectedYes(parentIsActiveDutyMilitary),null));
    }

    //applicant is reserve or national guard
    String parentIsReserveOrNationalGuard = (String) submission.getInputData().getOrDefault("parentInMilitaryReserveOrNationalGuard", "");
    if(!parentIsReserveOrNationalGuard.isBlank()){
      results.put("parentInMilitaryReserveOrNationalGuard", new SingleField("parentInMilitaryReserveOrNationalGuard", selectedYes(parentIsReserveOrNationalGuard),null));

    }

    Boolean experiencingHomelessness = (Boolean) submission.getInputData().getOrDefault("parentHomeExperiencingHomelessness[]", "no").equals(
        List.of("yes"));
    results.put("parentExperiencingHomelessness", new SingleField("parentExperiencingHomelessness", experiencingHomelessness.toString(), null));
    return results;
  }


  private static String selectedYes(String selected){
    if (selected.equals("Yes")){
      return "true";
    }else
      return "false";
  }


}
