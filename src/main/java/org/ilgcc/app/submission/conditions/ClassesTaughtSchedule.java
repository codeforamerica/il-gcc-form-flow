package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

/**
 * This condition will check to see if the applicant answered Yes/No
 * size.
 */
@Component
public class ClassesTaughtSchedule implements Condition {

  public Boolean run(Submission submission) {
    return SubmissionUtilities.getProgramSchedule(submission).equalsIgnoreCase("NO");
  }

}
