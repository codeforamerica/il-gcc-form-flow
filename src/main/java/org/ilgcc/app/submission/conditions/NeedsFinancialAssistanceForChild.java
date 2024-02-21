package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Component
public class NeedsFinancialAssistanceForChild implements Condition {

  @Override
  public Boolean run(Submission submission, String uuid) {
    var children = (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
    for(var child : children) {
      if(child.get("uuid").equals(uuid)) {
        return child.getOrDefault("needFinancialAssistanceForChild", "No").equals("Yes");
      }
    }

    return false;
  }
}
