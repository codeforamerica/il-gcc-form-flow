package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import org.ilgcc.app.utils.PreparerUtilities;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.ilgcc.app.utils.PreparerUtilities.flowIteratorPreparer;
import static org.ilgcc.app.utils.SubmissionUtilities.getDateInput;
import static org.ilgcc.app.utils.SubmissionUtilities.getDateInputWithDayOptional;
import static org.ilgcc.app.utils.SubmissionUtilities.selectedYes;

@Component
public class ParentPartnerPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var results = new HashMap<String, SubmissionField>();

        //PartnerLiveInHome
        String parentHasPartner = (String) submission.getInputData().getOrDefault("parentHasPartner", "");
        String parentHasQualifyingPartner = (String) submission.getInputData().getOrDefault("parentHasQualifyingPartner", "");
        if(parentHasPartner.equals("true")){
            if (parentHasQualifyingPartner.equals("true")) {
                results.put("partnerLiveInHome", new SingleField("partnerLiveInHome", "true", null));

            }else{
                results.put("partnerLiveInHome", new SingleField("partnerLiveInHome", "false", null));
            }
        }
        if(parentHasPartner.equals("false")){
            results.put("partnerLiveInHome", new SingleField("partnerLiveInHome", "false", null));
        }

        //partner dob
        Optional<LocalDate> parentPartnerDateOfBirth = getDateInput(submission, "parentPartnerBirth");
        if (parentPartnerDateOfBirth.isPresent()) {
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
        String partnerProgramStart = getDateInputWithDayOptional(submission, "partnerProgramStart");
        if(!partnerProgramStart.isEmpty()){
            results.put("partnerProgramStart", new SingleField("partnerProgramStart", partnerProgramStart, null));
        }

        String partnerProgramEnd = getDateInputWithDayOptional(submission, "partnerProgramEnd");

        if(!partnerProgramEnd.isEmpty()){
            results.put("partnerProgramEnd", new SingleField("partnerProgramEnd", partnerProgramEnd, null));
        }

        //active duty military
        String parentPartnerIsActiveDutyMilitary = (String) submission.getInputData().getOrDefault("parentPartnerIsServing", "");
        if (!parentPartnerIsActiveDutyMilitary.isBlank()) {
            results.put("parentPartnerIsServing",
                    new SingleField("parentPartnerIsServing", selectedYes(parentPartnerIsActiveDutyMilitary), null));
        }

        //applicant is reserve or national guard
        String parentPartnerIsReserveOrNationalGuard = (String) submission.getInputData()
                .getOrDefault("parentPartnerInMilitaryReserveOrNationalGuard", "");
        if (!parentPartnerIsReserveOrNationalGuard.isBlank()) {
            results.put("parentPartnerInMilitaryReserveOrNationalGuard",
                    new SingleField("parentPartnerInMilitaryReserveOrNationalGuard",
                            selectedYes(parentPartnerIsReserveOrNationalGuard), null));
        }

        var partnerJobsCompanyFields = List.of("partnerCompanyName", "partnerEmployerStreetAddress", "partnerEmployerCity",
                "partnerEmployerState", "partnerEmployerZipCode", "partnerEmployerPhoneNumber");
        Map<String, SubmissionField> partnerJobsData = flowIteratorPreparer(submission, "partnerJobs", partnerJobsCompanyFields);

        if (!partnerJobsData.isEmpty()) {
            results.putAll(partnerJobsData);
        }

        String educationTypePrefix = "PARTNER";
        String educationTypeField = PreparerUtilities.getEducationTypeFieldValue(
                (String) submission.getInputData().getOrDefault("partnerEducationType", ""), educationTypePrefix);
        results.put("partnerEducation", new SingleField("partnerEducation", educationTypeField, null));

        var partnerHighestEducation = submission.getInputData().getOrDefault("partnerHasBachelorsDegree", "");

        if(partnerHighestEducation.equals("true")){
            results.put("partnerEducationHighestLevel",
                    new SingleField("partnerEducationHighestLevel", "BA degree", null));
        }

        return results;
    }
}
