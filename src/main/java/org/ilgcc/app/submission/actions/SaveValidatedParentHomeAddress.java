package org.ilgcc.app.submission.actions;

import formflow.library.data.SubmissionRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class SaveValidatedParentHomeAddress extends SaveValidatedAddress {

    public SaveValidatedParentHomeAddress(SubmissionRepositoryService submissionRepositoryService) {
        super(submissionRepositoryService, "parentHome");
    }
}
