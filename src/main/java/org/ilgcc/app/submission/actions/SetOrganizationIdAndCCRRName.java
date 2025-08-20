package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.data.County;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.data.CCMSDataServiceImpl;
import org.ilgcc.app.submission.router.ApplicationRouterService;
import formflow.library.data.SubmissionRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetOrganizationIdAndCCRRName implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    ApplicationRouterService applicationRouterService;

    @Autowired
    CCMSDataServiceImpl ccmsDataServiceImpl;

    private static final String ORGANIZATION_ID_INPUT = "organizationId";
    private static final String ZIP_CODE_INPUT_NAME = "parentHomeZipCode";
    private static final String APPLICATION_COUNTY_INPUT_NAME = "applicationCounty";
    private static final String APPLICATION_ZIPCODE_INPUT_NAME = "applicationZipCode";
    private static final String APPLICANT_COUNTY_INPUT_NAME = "applicantAddressCounty";
    @Override
    public void run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();

        boolean experiencingHomelessness = inputData.getOrDefault("parentHomeExperiencingHomelessness[]", "no").equals(
                List.of("yes"));

        if (!experiencingHomelessness && hasValidValue(inputData, ZIP_CODE_INPUT_NAME)) {
            final String unvalidatedZip = (String) submission.getInputData().get(ZIP_CODE_INPUT_NAME);
            saveCountyFromZip(submission, unvalidatedZip);

            final Optional<ResourceOrganization> org = applicationRouterService.getOrganizationIdByZipCode(unvalidatedZip);

            if (org.isPresent()) {
                saveOrganizationIdAndNameAndPhoneNumber(submission, org.get());
                return;
            } else {
                log.info(String.format("Submission: %s has a zipCode (%s) without a matching organization id", submission.getId(),
                        unvalidatedZip));
            }
        }

        if (hasValidValue(inputData, APPLICATION_COUNTY_INPUT_NAME)) {
            final String applicationCounty = (String) submission.getInputData().get(APPLICATION_COUNTY_INPUT_NAME);
            Optional<ResourceOrganization> organization = applicationRouterService.getOrganizationByCountyName(applicationCounty);

            if (organization.isPresent()) {
                saveOrganizationIdAndNameAndPhoneNumber(submission, organization.get());
                return;
            } else {
                log.info(
                        String.format("Submission: %s has a countyName %s without a matching organization id", submission.getId(),
                                applicationCounty));
            }
        }

        if (hasValidValue(inputData, APPLICATION_ZIPCODE_INPUT_NAME)) {
            final String applicationZipCode = (String) submission.getInputData().get(APPLICATION_ZIPCODE_INPUT_NAME);
            final Optional<ResourceOrganization> org = applicationRouterService.getOrganizationIdByZipCode(
                    applicationZipCode);
            if (org.isPresent()) {
                saveOrganizationIdAndNameAndPhoneNumber(submission, org.get());
                return;
            }
        }

        log.info("Could not assign a caseload number to this application");
    }

    private void saveOrganizationIdAndNameAndPhoneNumber(Submission submission, ResourceOrganization org) {
        submission.getInputData().put(ORGANIZATION_ID_INPUT, org.getResourceOrgId());
        submission.getInputData().put("ccrrName", org.getName());
        submission.getInputData().put("ccrrPhoneNumber", org.getPhone());
        submissionRepositoryService.save(submission);
    }

    private void saveCountyFromZip(Submission submission, String zipCode) {
        Optional<County> county = ccmsDataServiceImpl.getCountyByZipCode(zipCode);
        if (county.isPresent()) {
            submission.getInputData().put(APPLICANT_COUNTY_INPUT_NAME, county.get().getCounty());
            submissionRepositoryService.save(submission);
        } else {
            log.info(String.format("could not assign a zip code to this application: %s", submission.getId()));
        }

    }

    private boolean hasValidValue(Map<String, Object> inputData, String inputKey) {
        return !inputData.getOrDefault(inputKey, "").toString().isBlank();
    }
}

