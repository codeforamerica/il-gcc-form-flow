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
public class SetPartnerClassWeeklyScheduleGroup extends SetWeeklyScheduleGroup {

  public SetPartnerClassWeeklyScheduleGroup(MessageSource messageSource) {
    super(messageSource);
  }

  @Override
  protected List<String> getWeeklySchedule(Submission submission) {
    Map<String, Object> inputData = submission.getInputData();
    if (!inputData.containsKey("partnerClassWeeklySchedule[]")) {
      return null;
    }
    return new ArrayList<>((List<String>) inputData.get("partnerClassWeeklySchedule[]"));
  }

  @Override
  protected List<String> getWeeklySchedule(Submission submission, String id) {
    throw new IllegalStateException("Not implemented");
  }
}
