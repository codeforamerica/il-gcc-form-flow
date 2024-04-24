package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class ClearHomeAddressWhenExperiencingHomelessnessIsSelected implements Action {
  @Override
  public void run(FormSubmission formSubmission, Submission submission){
    Map<String, Object> formData = formSubmission.formData;
    if(formData.containsKey("parentHomeExperiencingHomelessness[]") && formData.getOrDefault("parentHomeExperiencingHomelessness[]", List.of("")).equals(List.of("yes"))){
      clearParentHomeAddressValues(submission);
    }
  }

  private void clearParentHomeAddressValues(Submission submission){
    Map<String, Object> updatedInputData = (submission.getInputData());
    updatedInputData.put("parentHomeStreetAddress1", "");
    updatedInputData.put("parentHomeStreetAddress2", "");
    updatedInputData.put("parentHomeCity", "");
    updatedInputData.put("parentHomeState", "");
    updatedInputData.put("parentHomeZipCode", "");
  }
}
