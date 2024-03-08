package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParentIsExperiencingHomelessness implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return submission.getInputData().getOrDefault("parentHomeExperiencingHomelessness[]", "no").equals(List.of("yes"));
  }
}
