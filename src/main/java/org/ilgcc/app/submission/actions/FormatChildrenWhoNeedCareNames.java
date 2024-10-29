package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormatChildrenWhoNeedCareNames implements Action {
    
    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    
    @Override
    public void run(Submission providerSubmission) {
        Optional<UUID> clientID = ProviderSubmissionUtilities.getClientId(providerSubmission);
        if (clientID != null &&  clientID.isPresent()) {
            Submission clientSubmission = submissionRepositoryService.findById(clientID.get()).get();
            String formattedChildrenNames = ProviderSubmissionUtilities.formatChildNamesAsCommaSeperatedList(clientSubmission);
            providerSubmission.getInputData().put("childrenWhoNeedCareNames", formattedChildrenNames);
        }
    }
}
