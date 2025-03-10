package org.ilgcc.app.submission.actions;

import formflow.library.data.SubmissionRepositoryService;
import org.springframework.stereotype.Component;

@Component
public class SaveValidatedProviderResponseServiceAddress extends SaveValidatedAddress {
    public SaveValidatedProviderResponseServiceAddress(SubmissionRepositoryService submissionRepositoryService) {
        super(submissionRepositoryService, "providerResponseService");
    }
}
