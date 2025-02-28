package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChildAttendsOtherSchoolDuringDay implements Condition {

  @Override
  public Boolean run(Submission submission, String id) {
    Optional<Map<String, Object>> childSubflow = Optional.of(submission.getSubflowEntryByUuid("children", id));
    return childSubflow.get().getOrDefault("childAttendsOtherEd", "false").equals("true");
  }
}
