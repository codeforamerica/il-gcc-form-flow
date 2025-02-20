package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FindApplicationData implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Override
    public void run(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if(familySubmissionId.isPresent()){
           Optional<Submission> familySubmission = submissionRepositoryService.findById(familySubmissionId.get());
           providerSubmission.getInputData().put("clientResponse", ProviderSubmissionUtilities.getFamilySubmissionForProviderResponse(familySubmission));

           providerSubmission.getInputData().put("clientResponseChildren", ProviderSubmissionUtilities.getChildrenDataForProviderResponse(familySubmission.get()));
           submissionRepositoryService.save(providerSubmission);
        }
    }
}
