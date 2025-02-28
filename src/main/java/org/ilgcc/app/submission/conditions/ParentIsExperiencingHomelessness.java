package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.ilgcc.app.utils.AddressUtilities;

import org.springframework.stereotype.Component;


@Component
public class ParentIsExperiencingHomelessness implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return AddressUtilities.parentIsExperiencingHomelessness(submission.getInputData());
  }
}
