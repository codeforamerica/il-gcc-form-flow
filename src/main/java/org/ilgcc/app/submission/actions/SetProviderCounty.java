package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.CCMSDataServiceImpl;
import org.ilgcc.app.data.County;
import org.ilgcc.app.submission.router.ApplicationRouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class SetProviderCounty implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    ApplicationRouterService applicationRouterService;
    @Autowired
    CCMSDataServiceImpl ccmsDataServiceImpl;
    private static final String PROVIDER_ZIP_INPUT_NAME = "providerResponseServiceZipCode";
    private static final String PROVIDER_COUNTY_OUTPUT_NAME = "providerResponseAddressCounty";

    @Override
    public void run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        if (inputData.containsKey(PROVIDER_ZIP_INPUT_NAME)) {
            String providerZip = (String) inputData.get(PROVIDER_ZIP_INPUT_NAME);
            if (providerZip != null && providerZip.length() >= 5) {
                Optional<County> providerCountyOpt = ccmsDataServiceImpl.getCountyByZipCode(providerZip);
                providerCountyOpt.ifPresentOrElse(county -> { inputData.put(PROVIDER_COUNTY_OUTPUT_NAME, county.getCounty());
            },() -> inputData.put(PROVIDER_COUNTY_OUTPUT_NAME, null));
            }
        }
        boolean experiencingHomelessness = inputData.getOrDefault("parentHomeExperiencingHomelessness[]", "no").equals(
                List.of("yes"));
        submissionRepositoryService.save(submission);
    }
}