package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ParentPartnerQualifies implements Condition {

  @Override
  public Boolean run(Submission submission) {
    Map<String, Object> inputData = submission.getInputData();
     return inputData.getOrDefault("parentHasPartner", "false").toString().equalsIgnoreCase("true") &&
        inputData.getOrDefault("parentHasQualifyingPartner", "false").toString().equalsIgnoreCase("true");
  }
}