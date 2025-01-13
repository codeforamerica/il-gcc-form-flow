package org.ilgcc.app.submission.actions;

import formflow.library.data.Submission;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.DateUtilities;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SaveCCAPStartDate extends VerifyDate {
    static final String PREFIX = "ccapStart";
    static final String INPUT_NAME = "earliestChildcareStartDate";

    @Override
    public void run(Submission submission, String id) {
        log.info(String.format("Running %s", this.getClass().getName()));
        Map<String, Object> childSubflowData = submission.getSubflowEntryByUuid("children", id);
        String currentChildStartDate = DateUtilities.getFormattedDateFromMonthDateYearInputs(PREFIX, childSubflowData);

        if (DateUtilities.isDateInvalid(currentChildStartDate)) {
            return;
        }
        String formattedDate = DateUtilities.getFormattedDateFromMonthDateYearInputs(PREFIX, childSubflowData);
        childSubflowData.put(PREFIX + "Date", formattedDate);

        String earliestCCAPStart = (String) submission.getInputData().getOrDefault(INPUT_NAME, "");

        if (earliestCCAPStart == null || earliestCCAPStart.isBlank()) {
            submission.getInputData().put(INPUT_NAME, formattedDate);
        } else {
            String earliestDate = getEarliestDate(earliestCCAPStart, formattedDate);
            if (earliestDate != null) {
                submission.getInputData().put(INPUT_NAME, earliestDate);
            }
        }
    }
}
