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
public class SetOrganizationIdAndCCRRName implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    ApplicationRouterService applicationRouterService;

    private static final String ORGANIZATION_ID_INPUT = "organizationId";

//    private static final String VALIDATED_ZIPCODE_INPUT_NAME = "parentHomeZipCode_validated";
    private static final String UNVALIDATED_ZIPCODE_INPUT_NAME = "parentHomeZipCode";
    private static final String APPLICATION_COUNTY_INPUT_NAME = "applicationCounty";
    private static final String APPLICATION_ZIPCODE_INPUT_NAME = "applicationZipCode";

    @Override
    public void run(Submission submission) {
        //TODO: add back in once home address is validated
//        if (submission.getInputData().containsKey(VALIDATED_ZIPCODE_INPUT_NAME)) {
//            final String validatedZip = (String) submission.getInputData().get(VALIDATED_ZIPCODE_INPUT_NAME);
//            final Optional<String> organizationId = applicationRouterService.getOrganizationIdByZipCode(validatedZip);
//
//            if (organizationId.isPresent()) {
//                saveCaseLoadAndName(submission, organizationId.get());
//                return;
//            }
//        }

        if (submission.getInputData().containsKey(UNVALIDATED_ZIPCODE_INPUT_NAME)) {
            final String unvalidatedZip = (String) submission.getInputData().get(UNVALIDATED_ZIPCODE_INPUT_NAME);
            final Optional<String> organizationId = applicationRouterService.getOrganizationIdByZipCode(unvalidatedZip);

            if (organizationId.isPresent()) {
                saveCaseLoadAndName(submission, organizationId.get());
                return;
            }
        }

        if (submission.getInputData().containsKey(APPLICATION_COUNTY_INPUT_NAME)) {
            final String applicationCounty = (String) submission.getInputData().get(APPLICATION_COUNTY_INPUT_NAME);
            final Optional<ZipcodeOption> zipCode = CountyOption.getZipCodeFromCountyName(applicationCounty);

            if (zipCode.isPresent()) {
                Optional<String> organizationId = applicationRouterService.getOrganizationIdByZipCode(zipCode.get().getValue());
                saveCaseLoadAndName(submission, organizationId.get());
                return;
            }
        }

        if (submission.getInputData().containsKey(APPLICATION_ZIPCODE_INPUT_NAME)) {
            final String applicationZipCode = (String) submission.getInputData().get(APPLICATION_ZIPCODE_INPUT_NAME);
            final Optional<String> organizationId = applicationRouterService.getOrganizationIdByZipCode(applicationZipCode);
            if (organizationId.isPresent()) {
                saveCaseLoadAndName(submission, organizationId.get());
                return;
            }
        }

        log.info("Could not assign a caseload number to this application");
    }

    private void saveCaseLoadAndName(Submission submission, String organizationId) {
        submission.getInputData().put(ORGANIZATION_ID_INPUT, organizationId);
//       TODO: Set CCRR Name here
        submissionRepositoryService.save(submission);
    }
}

