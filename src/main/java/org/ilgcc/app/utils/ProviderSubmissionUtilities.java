package org.ilgcc.app.utils;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.utils.SubmissionUtilities.MM_DD_YYYY;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProviderSubmissionUtilities {

    public static Optional<UUID> getClientId(Submission providerSubmission) {
        if(providerSubmission.getInputData().containsKey("clientApplicationId")){
            String applicantID = (String) providerSubmission.getInputData().get("clientApplicationId");
            return Optional.of(UUID.fromString(applicantID));
        }
        return null;
    }
//
    public static Map<String, String> getApplicantSubmissionForProviderResponse(Optional<Submission> applicantSubmission){
        Map<String,String> applicationData = new HashMap<>();

        if(applicantSubmission.isPresent()){
            Map<String, Object> applicantInputs = applicantSubmission.get().getInputData();
            applicationData.put("response.confirmation-code", applicantSubmission.get().getShortCode());
            applicationData.put("response.parent-name", getApplicantName(applicantInputs));
            applicationData.putAll(getChildrenData(applicantSubmission.get()));



        }

//
        return applicationData;
    }

    private static String getApplicantName(Map<String, Object> applicantInputData){
        String firstName = SubmissionUtilities.applicantFirstName(applicantInputData);
        String lastName = (String) applicantInputData.get("parentLastName");

        return String.format("%s %s", firstName, lastName);
    }

    private static Map<String, String> getChildrenData(Submission applicantSubmission){
        Map<String,String> child = new HashMap<>();

        if(SubmissionUtilities.getChildrenNeedingAssistance(applicantSubmission).size() > 0){
            var firstChild = SubmissionUtilities.getChildrenNeedingAssistance(applicantSubmission).get(0);
            String firstName = (String) firstChild.get("childFirstName");
            String lastName = (String) firstChild.get("childLastName");

            child.put("response.child-name", String.format("%s %s", firstName, lastName));
            child.put("response.child-age", String.valueOf(childAge(firstChild)));


        }


//        Hours of care requested
//        Schedule

//        Start date of care requested:
//        "ccapStartDate": "01/01/2025",//        children.
//
//            response.hours=Hours of care requested:
//            response.start-date=Start date of care requested:
        return child;
    }

    private static Integer childAge(Map<String, Object> child){
        var bdayString = String.format("%s/%s/%s",
                child.get("childDateOfBirthMonth"),
                child.get("childDateOfBirthDay"),
                child.get("childDateOfBirthYear"));


        LocalDate dateOfBirth = LocalDate.parse(bdayString, MM_DD_YYYY);

        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }


}
