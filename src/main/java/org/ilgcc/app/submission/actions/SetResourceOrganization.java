package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getFamilySubmissionId;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.ilgcc.app.data.CCMSDataServiceImpl;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.submission.router.ApplicationRouterService;
import formflow.library.data.SubmissionRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.submission.router.ApplicationRoutingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetResourceOrganization implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    ApplicationRouterService applicationRouterService;

    @Autowired
    CCMSDataServiceImpl ccmsDataServiceImpl;

    @Autowired
    ApplicationRoutingServiceImpl applicationRouterServiceImpl;

    private static final String ORGANIZATION_ID_INPUT = "organizationId";
    private final String PROVIDER_NUMBER = "providerResponseProviderNumber";


    @Override
    public void run(Submission providerSubmission) {
        Map<String, Object> inputData = providerSubmission.getInputData();

        // Because of validation, providerResponseProviderNumber cannot be null or invalid
        BigInteger providerId = new BigInteger(inputData.get(PROVIDER_NUMBER).toString());
        Optional<ResourceOrganization> org = applicationRouterServiceImpl.getSiteAdministeredOrganizationByProviderId(
                providerId);

        if (org.isPresent()) {
            Optional<UUID> familySubmissionId = getFamilySubmissionId(providerSubmission);

            if(familySubmissionId.isPresent()){
                Optional<Submission> familySubmission = submissionRepositoryService.findById(familySubmissionId.get());
                if (familySubmission.isPresent()) {
                    familySubmission.get().getInputData().put(ORGANIZATION_ID_INPUT, org.get().getResourceOrgId());
                    familySubmission.get().getInputData().put("ccrrName", org.get().getName());
                    familySubmission.get().getInputData().put("ccrrPhoneNumber", org.get().getPhone());
                    submissionRepositoryService.save(familySubmission.get());
                }
            }
        }

    }

}