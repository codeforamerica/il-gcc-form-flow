package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.Optional;
import org.ilgcc.app.submission.router.ApplicationRouterService;
import formflow.library.data.SubmissionRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.submission.router.CCRR;
import org.ilgcc.app.utils.CountyOption;
import org.ilgcc.app.utils.ZipcodeOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetCaseLoadCodeAndCCRRName implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    ApplicationRouterService applicationRouterService;

    static private final String CASELOAD_INPUT_NAME = "caseloadCode";

    // We use parent mailing instead of home because this is the validated address and unhoused people may have a mailing address
    static private final String VALIDATED_ZIPCODE_INPUT_NAME = "parentMailingZipCode_validated";
    static private final String UNVALIDATED_ZIPCODE_INPUT_NAME = "parentMailingZipCode";
    static private final String APPLICATION_COUNTY_INPUT_NAME = "applicationCounty";
    static private final String APPLICATION_ZIPCODE_INPUT_NAME = "applicationZipCode";

    @Override
    public void run(Submission submission) {
        if (submission.getInputData().containsKey(VALIDATED_ZIPCODE_INPUT_NAME)) {
            final String validatedZip = (String) submission.getInputData().get(VALIDATED_ZIPCODE_INPUT_NAME);
            final Optional<String> caseLoadCode = applicationRouterService.getOrganizationIdByZipCode(validatedZip);

            if (caseLoadCode.isPresent()) {
                saveCaseLoadAndName(submission, caseLoadCode.get());
                return;
            }
        }

        if (submission.getInputData().containsKey(UNVALIDATED_ZIPCODE_INPUT_NAME)) {
            final String unvalidatedZip = (String) submission.getInputData().get(UNVALIDATED_ZIPCODE_INPUT_NAME);
            final Optional<String> caseLoadCode = applicationRouterService.getOrganizationIdByZipCode(unvalidatedZip);

            if (caseLoadCode.isPresent()) {
                saveCaseLoadAndName(submission, caseLoadCode.get());
                return;
            }
        }

        if (submission.getInputData().containsKey(APPLICATION_COUNTY_INPUT_NAME)) {
            final String applicationCounty = (String) submission.getInputData().get(APPLICATION_COUNTY_INPUT_NAME);
            final Optional<ZipcodeOption> zipCode = CountyOption.getZipCodeFromCountyName(applicationCounty);

            if (zipCode.isPresent()) {
                Optional<String> caseLoadCode = applicationRouterService.getOrganizationIdByZipCode(zipCode.get().getValue());
                saveCaseLoadAndName(submission, caseLoadCode.get());
                return;
            }
        }

        if (submission.getInputData().containsKey(APPLICATION_ZIPCODE_INPUT_NAME)) {
            final String applicationZipCode = (String) submission.getInputData().get(APPLICATION_ZIPCODE_INPUT_NAME);
            final Optional<String> caseLoadCode = applicationRouterService.getOrganizationIdByZipCode(applicationZipCode);
            if (caseLoadCode.isPresent()) {
                saveCaseLoadAndName(submission, caseLoadCode.get());
                return;
            }
        }

        log.info("Could not assign a caseload number to this application");
    }

    private void saveCaseLoadAndName(Submission submission, String caseLoadCode) {
        Optional<CCRR> ccrr = CCRR.findCCRRByCaseLoadCode(caseLoadCode);
        submission.getInputData().put(CASELOAD_INPUT_NAME, caseLoadCode);

//       TODO: Set CCRR Name here
        submissionRepositoryService.save(submission);
    }
}

