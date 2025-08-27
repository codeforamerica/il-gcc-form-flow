package org.ilgcc.app.submission.actions;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getFamilySubmissionId;
import static org.ilgcc.app.utils.SubmissionUtilities.setProviderResourceOrgId;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.submission.router.ApplicationRoutingServiceImpl;
import org.ilgcc.app.utils.SubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetResourceOrganization implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    
    @Autowired
    ApplicationRoutingServiceImpl applicationRouterServiceImpl;

    private static final String ORGANIZATION_ID_INPUT = "organizationId";
    private final String PROVIDER_NUMBER = "providerResponseProviderNumber";


    @Override
    public void run(Submission providerSubmission) {
        Map<String, Object> providerInputData = providerSubmission.getInputData();

        // Because of validation, providerResponseProviderNumber cannot be null or invalid
        BigInteger providerId = new BigInteger(providerInputData.get(PROVIDER_NUMBER).toString());
        Optional<ResourceOrganization> org = applicationRouterServiceImpl.getSiteAdministeredOrganizationByProviderId(providerId);

        if (org.isPresent()) {
            Optional<UUID> familySubmissionId = getFamilySubmissionId(providerSubmission);

            if (familySubmissionId.isPresent()) {
                Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId.get());
                if (familySubmissionOptional.isPresent()) {
                    Submission familySubmission = familySubmissionOptional.get();
                    if (familySubmission.getInputData().containsKey("providers")) {
                        setProviderResourceOrgId(
                                familySubmission,
                                providerInputData.getOrDefault("currentProviderUuid", "").toString(),
                                org.get().getResourceOrgId().toString());
                        submissionRepositoryService.save(familySubmission);
                    }
                    if (SubmissionUtilities.isPreMultiProviderApplicationWithSingleProvider(familySubmission) || 
                            SubmissionUtilities.allProvidersBelongToTheSameSiteAdministeredResourceOrganization(familySubmission)) {
                        familySubmission.getInputData().put(ORGANIZATION_ID_INPUT, org.get().getResourceOrgId());
                        familySubmission.getInputData().put("ccrrName", org.get().getName());
                        familySubmission.getInputData().put("ccrrPhoneNumber", org.get().getPhone());
                        submissionRepositoryService.save(familySubmission);
                    }
                }
            }
        }

    }

}