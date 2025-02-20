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
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            Submission familySubmission = submissionRepositoryService.findById(familySubmissionId.get()).get();
            String formattedChildrenNames = ProviderSubmissionUtilities.formatChildNamesAsCommaSeperatedList(familySubmission);
            providerSubmission.getInputData().put("childrenWhoNeedCareNames", formattedChildrenNames);
        }
    }
}
