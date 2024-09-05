package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ilgcc.app.utils.GenderOption;
import org.ilgcc.app.utils.RaceEthnicityOption;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import static org.ilgcc.app.utils.PreparerUtilities.formatYesNo;

@Component
public class NeedChildcareForChildren implements SubmissionFieldPreparer {

  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    var results = new HashMap<String, SubmissionField>();
    int iteration = 1;
    String earliestCCAPStart= "";
    for (var child : SubmissionUtilities.firstFourChildrenNeedingAssistance(submission)) {
      results.put("childFirstName_" + iteration, new SingleField("childFirstName", (String) child.getOrDefault("childFirstName", ""), iteration));
      results.put("childLastName_" + iteration, new SingleField("childLastName", (String) child.getOrDefault("childLastName", ""), iteration));
      results.put("childDateOfBirth_" + iteration, new SingleField("childDateOfBirth", formatChildDateOfBirth(child), iteration));
      results.put("childRaceEthnicity_" + iteration, new SingleField("childRaceEthnicity", formatChildRaceEthnicity((List) child.getOrDefault("childRaceEthnicity[]", List.of())), iteration));
      results.put("childGender_" + iteration, new SingleField("childGender", formatChildGender((List) child.getOrDefault("childGender[]", List.of())), iteration));
      results.put("childSpecialNeeds_" + iteration, new SingleField("childSpecialNeeds", formatYesNo((String) child.getOrDefault("childHasDisability", "")), iteration));
      results.put("childUSCitizen_" + iteration, new SingleField("childUSCitizen", formatYesNo((String) child.getOrDefault("childIsUsCitizen", "")), iteration));
      results.put("childCareChildInSchool_" + iteration, new SingleField("childCareChildInSchool", (String) child.getOrDefault("childAttendsOtherEd", ""), iteration));
      results.put("childRelationship_"+ iteration, new SingleField("childRelationship", (String) child.get("childRelationship"), iteration));
      earliestCCAPStart = getEarliestCCAPStartDate(earliestCCAPStart, (String) child.getOrDefault("ccapStartDate", ""), formatter);
      iteration++;
    }
    results.put("childcareStartDate", new SingleField("childcareStartDate", earliestCCAPStart, null));
    return results;
  }

  private String formatChildDateOfBirth(Map<String, Object> child) {
    return String.format("%s/%s/%s",
        child.get("childDateOfBirthMonth"),
        child.get("childDateOfBirthDay"),
        child.get("childDateOfBirthYear"));
  }

  private String formatChildRaceEthnicity(List raceEthnicity){
    if(raceEthnicity.isEmpty()){
      return "";
    }
    else if(raceEthnicity.equals(List.of("NONE"))){
        return "X";
    } else {
      return String.join(", ", raceEthnicity.stream().map(name -> RaceEthnicityOption.getPdfValueByName(String.valueOf(name))).distinct().sorted().toList());
    }
  }

  private String formatChildGender(List gender){
    if(gender.isEmpty() || gender.equals(List.of("NONE"))) {
      return "";
    } else {
      return String.join(",", gender.stream().map(name -> GenderOption.getPdfValueByName(String.valueOf(name))).distinct().sorted().toList());
    }
  }

  private String getEarliestCCAPStartDate(String earliestCCAPStartDate, String childCCAPStartDate, DateTimeFormatter formatter){
    if(earliestCCAPStartDate.isBlank()){
      return childCCAPStartDate;
    }
    if(childCCAPStartDate.isBlank()){
      return earliestCCAPStartDate;
    }

    Optional<LocalDate> earliestDate = Optional.of(LocalDate.parse((addLeadingZerosToDateString(earliestCCAPStartDate)), formatter));
    Optional<LocalDate> childcareStartDate = Optional.of(LocalDate.parse(addLeadingZerosToDateString(childCCAPStartDate), formatter));
    return earliestDate.get().isBefore(childcareStartDate.get()) ? earliestCCAPStartDate : childCCAPStartDate;
  }
  private String addLeadingZerosToDateString(String dateStr){
    String pattern = "(\\d{1,2})/(\\d{1,2})/(\\d{4})";
    Pattern regex = Pattern.compile(pattern);
    Matcher matcher = regex.matcher(dateStr);
    try {
      if (matcher.matches()) {
        String month = matcher.group(1);
        String day = matcher.group(2);
        String year = matcher.group(3);

        String formattedMonth = String.format("%02d", Integer.parseInt(month));
        String formattedDay = String.format("%02d", Integer.parseInt(day));

        return formattedMonth + "/" + formattedDay + "/" + year;
      }
    }catch (Exception e) {
      return "";
    }
    return "";
  }
}
