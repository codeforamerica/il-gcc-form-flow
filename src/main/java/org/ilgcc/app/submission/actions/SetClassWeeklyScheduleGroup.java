package org.ilgcc.app.submission.actions;

import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SetClassWeeklyScheduleGroup extends SetWeeklyScheduleGroup {

  public SetClassWeeklyScheduleGroup(MessageSource messageSource) {
    super(messageSource);
  }

  @Override
  protected List<String> getWeeklySchedule(Submission submission) {
    Map<String, Object> inputData = submission.getInputData();
    if (!inputData.containsKey("weeklySchedule[]")) {
      return null;
    }
    return new ArrayList<>((List<String>) inputData.get("weeklySchedule[]"));
  }

  @Override
  protected List<String> getWeeklySchedule(Submission submission, String id) {
    throw new IllegalStateException("Not implemented");
  }
}
