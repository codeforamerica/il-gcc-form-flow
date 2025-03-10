package org.ilgcc.app.submission.actions;

import formflow.library.data.SubmissionRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class SaveValidatedProviderResponseMailingAddress extends SaveValidatedAddress {
    public SaveValidatedProviderResponseMailingAddress(SubmissionRepositoryService submissionRepositoryService) {
        super(submissionRepositoryService, "providerMailing");
    }
}
