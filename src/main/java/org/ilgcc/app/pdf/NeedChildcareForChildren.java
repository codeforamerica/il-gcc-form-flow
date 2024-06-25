package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import static org.ilgcc.app.utils.PreparerUtilities.formatYesNo;

@Component
public class NeedChildcareForChildren implements SubmissionFieldPreparer {
  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    var results = new HashMap<String, SubmissionField>();
    int iteration = 1;
    String earliestCCAPStart= "";
    for (var child : SubmissionUtilities.getChildrenNeedingAssistance(submission)) {
      results.put(getUniqueKey(), new SingleField("childFirstName", (String) child.getOrDefault("childFirstName", ""), iteration));
      results.put(getUniqueKey(), new SingleField("childLastName", (String) child.getOrDefault("childLastName", ""), iteration));
      results.put(getUniqueKey(), new SingleField("childDateOfBirth", formatChildDateOfBirth(child), iteration));
      results.put("childSpecialNeeds_" + iteration, new SingleField("childSpecialNeeds", formatYesNo((String) child.getOrDefault("childHasDisability", "")), iteration));
      results.put("childUSCitizen_" + iteration, new SingleField("childUSCitizen", formatYesNo((String) child.getOrDefault("childIsUsCitizen", "")), iteration));
      results.put("childCareChildInSchool_" + iteration, new SingleField("childCareChildInSchool", (String) child.getOrDefault("childAttendsOtherEd", ""), iteration));
      earliestCCAPStart = getEarliestCCAPStartDate(earliestCCAPStart, (String) child.getOrDefault("ccapStartDate", ""), formatter);
      iteration++;
    }

results.put("childCareStartDate", new SingleField("childCareStartDate", earliestCCAPStart, null));
    return results;
  }



  private String getUniqueKey() {
    return getClass().getName() + new Random();
  }

  private String formatChildDateOfBirth(Map<String, Object> child) {
    return String.format("%s/%s/%s",
        child.get("childDateOfBirthMonth"),
        child.get("childDateOfBirthDay"),
        child.get("childDateOfBirthYear"));
  }

  private String getEarliestCCAPStartDate(String earliestCCAPStartDate, String childCCAPStartDate, DateTimeFormatter formatter){
    if(earliestCCAPStartDate.isBlank()){
      return childCCAPStartDate;
    }

    Optional<LocalDate> earliestDate = Optional.of(LocalDate.parse((addLeadingZerosToDateString(earliestCCAPStartDate)), formatter));
    Optional<LocalDate> childCareStartDate = Optional.of(LocalDate.parse(addLeadingZerosToDateString(childCCAPStartDate), formatter));
    return earliestDate.get().isBefore(childCareStartDate.get()) ? earliestCCAPStartDate : childCCAPStartDate;
//    try {
//      LocalDate earliestDate = LocalDate.parse(earliestCCAPStartDate, formatter);
//      LocalDate childCareStartDate = LocalDate.parse(childCCAPStartDate, formatter);
//      return earliestDate.isBefore(childCareStartDate) ? earliestCCAPStartDate : childCCAPStartDate;
//
//    }catch (DateTimeException e) {
//      return earliestCCAPStartDate;
//    }
  }
  private String addLeadingZerosToDateString(String dateStr){
    String pattern = "(\\d{1,2})/(\\d{1,2})/(\\d{4})";
    Pattern regex = Pattern.compile(pattern);
    Matcher matcher = regex.matcher(dateStr);

    if (matcher.matches()) {
      String month = matcher.group(1);
      String day = matcher.group(2);
      String year = matcher.group(3);

      String formattedMonth = String.format("%02d", Integer.parseInt(month));
      String formattedDay = String.format("%02d", Integer.parseInt(day));

      return formattedMonth + "/" + formattedDay + "/" + year;
    } else {
      return "";
    }
  }
}
