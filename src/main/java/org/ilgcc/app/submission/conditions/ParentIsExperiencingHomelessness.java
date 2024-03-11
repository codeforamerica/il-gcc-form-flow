package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParentIsExperiencingHomelessness implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return SubmissionUtilities.parentIsExperiencingHomelessness(submission.getInputData());
  }
}
