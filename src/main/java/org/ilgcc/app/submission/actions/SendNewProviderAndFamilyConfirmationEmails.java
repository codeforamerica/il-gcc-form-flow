package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendProviderRespondedConfirmationEmail;
import org.ilgcc.app.email.SendNewProviderAgreesToCareFamilyConfirmationEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendNewProviderAndFamilyConfirmationEmails implements Action {

    @Autowired
    SendNewProviderAgreesToCareFamilyConfirmationEmail sendNewProviderAgreesToCareFamilyConfirmationEmail;

    @Autowired
    SendProviderRespondedConfirmationEmail sendProviderRespondedConfirmationEmail;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Override
    public void run(Submission providerSubmission) {
        if (providerSubmissionIsActive(providerSubmission)) {
            sendNewProviderAgreesToCareFamilyConfirmationEmail.send(providerSubmission);
            sendProviderRespondedConfirmationEmail.send(providerSubmission);
        }
    }

    public boolean providerSubmissionIsActive(Submission providerSubmission) {
        Optional<Submission> familySubmissionOptional = getFamilySubmission(providerSubmission);
        String currentProviderUuid = (String) providerSubmission.getInputData().getOrDefault("currentProviderUuid", "");
        if (familySubmissionOptional.isPresent() && !currentProviderUuid.isEmpty()) {
            return !(ProviderSubmissionUtilities.hasProviderApplicationExpired(familySubmissionOptional.get(), providerSubmission));
        }
        return true;
    }

    public Optional<Submission> getFamilySubmission(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            return submissionRepositoryService.findById(familySubmissionId.get());
        }
        return Optional.empty();
    }
}
