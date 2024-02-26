package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SubmissionUtilities.MM_DD_YYYY;

@Component
public class IsFiveOrOlder implements Condition {
  private static final LocalDate FIVE_YEARS_AGO = LocalDate.now().minusYears(5);

  @Override
  public Boolean run(Submission submission, String uuid) {
    var children = (List<Map<String, Object>>) submission.getInputData().getOrDefault("children", emptyList());
    for (var child : children) {
      if (child.get("uuid").equals(uuid)) {
        var bday = getChildBirthday(child);
        return bday.isBefore(FIVE_YEARS_AGO);
      }
    }

    return false;
  }

  private LocalDate getChildBirthday(Map<String, Object> child) {
    var bdayString = String.format("%s/%s/%s",
        child.get("childDateOfBirthMonth"),
        child.get("childDateOfBirthDay"),
        child.get("childDateOfBirthYear"));
    return LocalDate.parse(bdayString, MM_DD_YYYY);
  }
}