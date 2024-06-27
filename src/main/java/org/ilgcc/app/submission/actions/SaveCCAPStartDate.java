package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.DateUtilities;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SaveCCAPStartDate implements Action {

  @Override
  public void run(Submission submission, String id) {
    log.info(String.format("Running %s", this.getClass().getName()));
    String prefix = "ccapStart";
    Map<String, Object> childSubflowData = submission.getSubflowEntryByUuid("children", id);
    String parentDateString = DateUtilities.getFormattedDateFromMonthDateYearInputs(prefix, childSubflowData);

    DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
    if (DateUtilities.isDateInvalid(parentDateString)) {
      return;
    }
    String key = String.format("%s%s", prefix, "Date");
    String formattedDate = dtf.print(dtf.parseDateTime(parentDateString));
    childSubflowData.put(key, formattedDate);
  }

}
