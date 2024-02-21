package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class NeedsFinancialAssistanceForChild implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return submission.getInputData().getOrDefault("needFinancialAssistanceForChild", "No").equals("Yes");
  }
}
