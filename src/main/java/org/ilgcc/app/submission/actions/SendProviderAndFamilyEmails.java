package org.ilgcc.app.submission.actions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendProviderAgreesToCareFamilyConfirmationEmail;
import org.ilgcc.app.email.SendProviderRespondedConfirmationEmail;
import org.ilgcc.app.email.SendProviderDeclinesCareFamilyConfirmationEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import formflow.library.config.submission.Action;
@Component
@Slf4j
public class SendProviderAndFamilyEmails implements Action {

    @Autowired
    SendProviderRespondedConfirmationEmail sendProviderRespondedConfirmationEmail;

    @Autowired
    SendProviderAgreesToCareFamilyConfirmationEmail sendProviderAgreesToCareFamilyConfirmationEmail;

    @Autowired
    SendProviderDeclinesCareFamilyConfirmationEmail sendProviderDeclinesCareFamilyConfirmationEmail;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Override
    public void run(Submission providerSubmission) {
        // If a provider is an existing provider that has done CCAP stuff before, send emails
        // New Provider Registration will send the emails later
        // if the providerSubmission has expired then we should not send the emails linked to this action

        if (!ProviderSubmissionUtilities.isProviderRegistering(providerSubmission) && providerSubmissionIsActive(providerSubmission)) {
            sendProviderAgreesToCareFamilyConfirmationEmail.send(providerSubmission);
            sendProviderDeclinesCareFamilyConfirmationEmail.send(providerSubmission);
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
