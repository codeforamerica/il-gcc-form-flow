package org.ilgcc.app.submission.actions;

import formflow.library.data.SubmissionRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class SaveValidatedParentMailingAddress extends SaveValidatedAddress {

    public SaveValidatedParentMailingAddress(SubmissionRepositoryService submissionRepositoryService) {
        super(submissionRepositoryService, "parentMailing");
    }
}
