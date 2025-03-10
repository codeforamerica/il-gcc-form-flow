package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getFamilySubmissionId;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import org.ilgcc.app.data.CCMSDataServiceImpl;
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
        Optional<BigInteger> resourceOrgId = applicationRouterServiceImpl.getSiteAdministeredOrganizationIdByProviderId(
                providerId);

        if (resourceOrgId.isPresent()) {
            Optional<Submission> familySubmissionOptional = getFamilySubmission(providerSubmission);
            familySubmissionOptional.get().getInputData().put(ORGANIZATION_ID_INPUT, resourceOrgId.get());
            submissionRepositoryService.save(familySubmissionOptional.get());
        }
    }


    private Optional<Submission> getFamilySubmission(Submission providerSubmission) {
        if (getFamilySubmissionId(providerSubmission).isPresent()) {
            return submissionRepositoryService.findById(getFamilySubmissionId(providerSubmission).get());
        } else {
            log.error("Provider application {} is missing a family application", providerSubmission.getId());
            return Optional.empty();
        }
    }

}