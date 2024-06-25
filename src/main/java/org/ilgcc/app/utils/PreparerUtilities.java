package org.ilgcc.app.utils;

import static java.util.Collections.emptyList;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PreparerUtilities {

  /**
   * Formats subflow data into proper PDF format with iterators
   *
   * @param submission the submission from which the data comes from
   * @param subflowName takes the key of the subflow
   * @param fieldNames are the keys within the suflow that need to be mapped
   * @return a map of string and submission Field or an empty Mao
   */

  public static Map<String, SubmissionField> flowIteratorPreparer(Submission submission, String subflowName, List<String> fieldNames) {
    Map<String, SubmissionField> fields = new HashMap<>();
    int iteration = 1;
    var subflow = ((List<Map<String, Object>>) submission.getInputData().getOrDefault(subflowName, emptyList())).stream().toList();
    for (var item : subflow) {
      for(var field : fieldNames){
        fields.put(field+"_"+iteration, new SingleField(field, (String) item.getOrDefault(field,""), iteration));
      }

      iteration++;
    }
    return fields;
  }
  public static String formatYesNo(String value) {
    return switch (value) {
      case "Yes" -> "true";
      case "No" -> "false";
      default -> "";
    };
  }

  public static Double numberValueOf(String incomeValue){
    if(!incomeValue.isEmpty()){
      return Double.parseDouble(incomeValue);
    } else {
      return 0.0;
    }
  }
}
