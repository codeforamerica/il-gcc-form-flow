package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SubmissionUtilities.allChildcareSchedulesAreForTheSameProvider;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HasMultipleProviders implements Condition {

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    @Override
    public Boolean run(Submission providerSubmission) {
        return hasMultipleProviders(providerSubmission);
    }

    @Override
    public Boolean run(Submission providerSubmission, String uuid) {
        return hasMultipleProviders(providerSubmission);
    }

    @Override
    public Boolean run(Submission providerSubmission, String uuid, String repeatsForUuid) {
        return hasMultipleProviders(providerSubmission);
    }

    @NotNull
    private Boolean hasMultipleProviders(Submission providerSubmission) {
        Optional<String> familySubmissionShortCode = ProviderSubmissionUtilities.getFamilySubmissionShortCode(providerSubmission);

        if (familySubmissionShortCode.isEmpty()) {
            log.warn("No family submission short code was found for provider submission: {}.", providerSubmission.getId());
            return false; // If we have no shortcode, we can't do anything
        }
        Optional<Submission> familySubmissionOptional =
                submissionRepositoryService.findByShortCode(familySubmissionShortCode.get().toUpperCase());
        if (familySubmissionOptional.isEmpty()) {
            log.warn("No family submission found for the provider submission: {}.", providerSubmission.getId());
            return false; // If we have no family submission, we can't do anything
        }

        return !allChildcareSchedulesAreForTheSameProvider(familySubmissionOptional.get().getInputData());
    }
}