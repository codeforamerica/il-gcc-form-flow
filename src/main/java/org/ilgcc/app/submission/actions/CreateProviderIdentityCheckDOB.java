package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.DateUtilities;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateProviderIdentityCheckDOB implements Action {

  @Override
  public void run(FormSubmission formSubmission, Submission submission) {
    log.info(String.format("Running %s", this.getClass().getName()));
    String prefix = "providerIdentityCheckDateOfBirth";
    Map<String, Object> inputData = formSubmission.getFormData();
    DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
    String parentDateString = String.format("%s/%s/%s",
        inputData.get(prefix + "Month"),
        inputData.get(prefix + "Day"),
        inputData.get(prefix + "Year"));

    if (DateUtilities.isDateInvalid(parentDateString)) {
      return;
    }

    String formattedDate = dtf.print(dtf.parseDateTime(parentDateString));
    formSubmission.formData.put(prefix + "Date", formattedDate);
  }
}
