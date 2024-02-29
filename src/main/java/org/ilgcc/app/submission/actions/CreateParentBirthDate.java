package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateParentBirthDate implements Action {

  @Override
  public void run(FormSubmission formSubmission, Submission submission) {
    log.info(String.format("Running %s", this.getClass().getName()));
    String prefix = "parentBirth";
    Map<String, Object> inputData = formSubmission.getFormData();
    DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
    String parentDateString = String.format("%s/%s/%s",
        (String) inputData.get(prefix + "Month"),
        (String) inputData.get(prefix + "Day"),
        (String) inputData.get(prefix + "Year"));

    if (isDateInvalid(parentDateString)) {
      return;
    }

    String formattedDate = dtf.print(dtf.parseDateTime(parentDateString));
    formSubmission.formData.put(prefix + "Date", formattedDate);
  }

  protected boolean isDateInvalid(String date) {
    try {
      DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");

      dtf.parseDateTime(date);
    } catch (Exception e) {
      return true;
    }
    return false;
  }
}
