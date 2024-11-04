package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class HasSupportedZipCode implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return submission.getInputData().getOrDefault("hasValidZipCode", "false").equals("true");
  }
}
