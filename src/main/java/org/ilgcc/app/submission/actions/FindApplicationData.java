package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpSession;
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
        Optional<UUID> clientID = ProviderSubmissionUtilities.getClientId(providerSubmission);
        if(clientID != null &&  clientID.isPresent()){
           Optional<Submission> clientSubmission = submissionRepositoryService.findById(clientID.get());
           providerSubmission.getInputData().put("clientResponse", ProviderSubmissionUtilities.getApplicantSubmissionForProviderResponse(clientSubmission));
           providerSubmission.getInputData().put("clientResponseChildren", ProviderSubmissionUtilities.getChildrenDataForProviderResponse(clientSubmission.get()));
        }
    }
}
