package org.ilgcc.app.submission.conditions;

import static org.ilgcc.app.utils.SubmissionUtilities.isMultiProviderApplication;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MultiProviderIsEnabledAndHasMultipleProviders implements Condition {

    @Value("${il-gcc.enable-multiple-providers}")
    private boolean enableMultipleProviders;
    
    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    @Override
    public Boolean run(Submission submission) {
        return multiproviderIsEnabledAndIsNotSingleProviderApplication(submission);
    }
    
    @Override
    public Boolean run(Submission submission, String uuid) {
        return multiproviderIsEnabledAndIsNotSingleProviderApplication(submission);
    }

    @Override
    public Boolean run(Submission submission, String uuid, String repeatsForUuid) {
        return multiproviderIsEnabledAndIsNotSingleProviderApplication(submission);
    }

    @NotNull
    private Boolean multiproviderIsEnabledAndIsNotSingleProviderApplication(Submission submission) {
        Optional<String> familySubmissionShortCode = ProviderSubmissionUtilities.getFamilySubmissionShortCode(submission);
        
        if (familySubmissionShortCode.isEmpty()) {
            log.warn("No family submission short code was found for provider submission: {}.", submission.getId());
            return false; // If we have no shortcode, we can't do anything
        }
        Optional<Submission> familySubmissionOptional = submissionRepositoryService.findByShortCode(familySubmissionShortCode.get().toUpperCase());
        if (familySubmissionOptional.isEmpty()) {
            log.warn("No family submission found for the provider submission: {}.", submission.getId());
            return false; // If we have no family submission, we can't do anything
        }

        Submission familySubmission = familySubmissionOptional.get();

        return enableMultipleProviders && isMultiProviderApplication(familySubmission);
    }
}
