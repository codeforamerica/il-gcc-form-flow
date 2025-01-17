package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.Optional;
import org.ilgcc.app.submission.router.ApplicationRouterService;
import formflow.library.data.SubmissionRepositoryService;

import lombok.extern.slf4j.Slf4j;


import org.ilgcc.app.utils.CountyOption;
import org.ilgcc.app.utils.ZipcodeOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetCCRR implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    static final String CASELOAD_INPUT_NAME = "caseloadCode";

    // We use parent mailing instead of home because this is the validated address and unhoused people may have a mailing address
    static final String VALIDATED_ZIPCODE_INPUT_NAME =  "parentMailingZipCode_validated";
    static final String UNVALIDATED_ZIPCODE_INPUT_NAME =  "parentMailingZipCode";
    static final String APPLICATION_COUNTY_INPUT_NAME = "applicationCounty";
    static final String APPLICATION_ZIPCODE_INPUT_NAME = "applicationZipCode";

    @Override
    public void run(Submission submission) {
        if(submission.getInputData().containsKey(VALIDATED_ZIPCODE_INPUT_NAME)){
            final String validatedZip = (String) submission.getInputData().getOrDefault(VALIDATED_ZIPCODE_INPUT_NAME, "");
            final Optional<String> caseLoadCode = ApplicationRouterService.getOrganizationIdByZipCode(validatedZip);

            if(caseLoadCode.isPresent()){
                submission.getInputData().put(CASELOAD_INPUT_NAME, caseLoadCode.get());
                submissionRepositoryService.save(submission);
                return;
            }
        }

        if(submission.getInputData().containsKey(UNVALIDATED_ZIPCODE_INPUT_NAME)){
            final String unvalidatedZip = (String) submission.getInputData().getOrDefault(UNVALIDATED_ZIPCODE_INPUT_NAME, "");
            final Optional<String> caseLoadCode = ApplicationRouterService.getOrganizationIdByZipCode(unvalidatedZip);

            if(caseLoadCode.isPresent()){
                submission.getInputData().put(CASELOAD_INPUT_NAME, caseLoadCode.get());
                submissionRepositoryService.save(submission);
                return;
            }
        }

        if(submission.getInputData().containsKey(APPLICATION_COUNTY_INPUT_NAME)){
            final String applicationCounty = (String) submission.getInputData().getOrDefault(APPLICATION_COUNTY_INPUT_NAME, "");
            final Optional<ZipcodeOption> zipCode = CountyOption.getZipCodeFromCountyName(applicationCounty);

            if(zipCode.isPresent()){
                Optional<String> caseLoadCode = ApplicationRouterService.getOrganizationIdByZipCode(zipCode.get().getValue());
                submission.getInputData().put(CASELOAD_INPUT_NAME, caseLoadCode.get());
                submissionRepositoryService.save(submission);
                return;
            }
        }

        if(submission.getInputData().containsKey(APPLICATION_ZIPCODE_INPUT_NAME)){
            final String applicationZipCode = (String) submission.getInputData().get(APPLICATION_ZIPCODE_INPUT_NAME);
            final Optional<String> caseLoadCode = ApplicationRouterService.getOrganizationIdByZipCode(applicationZipCode);
            if(caseLoadCode.isPresent()){
                submission.getInputData().put(CASELOAD_INPUT_NAME, caseLoadCode.get());
                submissionRepositoryService.save(submission);
                return;
            }
        }

        log.info("Could not assign a caseload number to this application");

    }
}

