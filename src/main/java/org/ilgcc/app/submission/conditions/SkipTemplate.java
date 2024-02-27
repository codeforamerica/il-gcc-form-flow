package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

/**
 * This is a false-Condition to skip the pages that have not yet been implemented.
 */
@Component
public class SkipTemplate implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return false;
  }

  @Override
  public Boolean run(Submission submission, String id) {
    return false;
  }
}
