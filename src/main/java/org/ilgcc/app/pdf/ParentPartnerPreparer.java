package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Integer.parseInt;
import static org.ilgcc.app.utils.PreparerUtilities.flowIteratorPreparer;
import static org.ilgcc.app.utils.SubmissionUtilities.getDateInput;
import static org.ilgcc.app.utils.SubmissionUtilities.selectedYes;

@Component
public class ParentPartnerPreparer implements SubmissionFieldPreparer {

  @Override
  public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
    var results= new HashMap<String, SubmissionField>();
    //partner dob
    Optional<LocalDate> parentPartnerDateOfBirth = getDateInput(submission, "parentPartnerBirth");
    if (parentPartnerDateOfBirth.isPresent()){
      LocalDate dob = parentPartnerDateOfBirth.get();
      results.put(
              "parentPartnerDateOfBirth",
              new SingleField(
                      "parentPartnerDateOfBirth",
                      dob.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                      null
              )
      );
    }
    //Term Start Date
    Optional<LocalDate> partnerProgramStart = getDateInput(submission, "partnerProgramStart");
    if (partnerProgramStart.isPresent()){
      LocalDate date = partnerProgramStart.get();
      results.put(
          "partnerProgramStart",
          new SingleField(
              "partnerProgramStart",
              date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
              null
          )
      );
    }

    //Term End Date
    Optional<LocalDate> partnerProgramEnd = getDateInput(submission, "partnerProgramEnd");
    if (partnerProgramEnd.isPresent()){
      LocalDate date = partnerProgramEnd.get();
      results.put(
          "partnerProgramEnd",
          new SingleField(
              "partnerProgramEnd",
              date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
              null
          )
      );
    }

    //active duty military
    String parentPartnerIsActiveDutyMilitary = (String) submission.getInputData().getOrDefault("parentPartnerIsServing", "");
    if (!parentPartnerIsActiveDutyMilitary.isBlank()){
      results.put("parentPartnerIsServing", new SingleField("parentPartnerIsServing", selectedYes(parentPartnerIsActiveDutyMilitary),null));
    }

    //applicant is reserve or national guard
    String parentPartnerIsReserveOrNationalGuard = (String) submission.getInputData().getOrDefault("parentPartnerInMilitaryReserveOrNationalGuard", "");
    if(!parentPartnerIsReserveOrNationalGuard.isBlank()){
      results.put("parentPartnerInMilitaryReserveOrNationalGuard", new SingleField("parentPartnerInMilitaryReserveOrNationalGuard", selectedYes(parentPartnerIsReserveOrNationalGuard),null));
    }

    var partnerJobsCompanyFields = List.of("partnerCompanyName", "partnerEmployerStreetAddress", "partnerEmployerCity", "partnerEmployerState", "partnerEmployerZipCode", "partnerEmployerPhoneNumber");
    Map<String, SubmissionField> partnerJobsData = flowIteratorPreparer(submission, "partnerJobs", partnerJobsCompanyFields);

    if(!partnerJobsData.isEmpty()){
      results.putAll(partnerJobsData);
    }

    return results;
  }
}
