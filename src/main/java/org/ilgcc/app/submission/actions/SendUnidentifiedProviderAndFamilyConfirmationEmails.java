package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendProviderDidNotRespondToFamilyEmail;
import org.ilgcc.app.email.SendUnidentifiedProviderConfirmationEmail;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendUnidentifiedProviderAndFamilyConfirmationEmails implements Action {

    @Autowired
    SendProviderDidNotRespondToFamilyEmail sendProviderDidNotRespondToFamilyEmail;

    @Autowired
    SendUnidentifiedProviderConfirmationEmail sendUnidentifiedProviderConfirmationEmail;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Override
    public void run(Submission submission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(submission);
        if (familySubmissionId.isPresent()) {
            Optional<Submission> familySubmission = submissionRepositoryService.findById(familySubmissionId.get());
            if (familySubmission.isPresent()) {
                sendProviderDidNotRespondToFamilyEmail.send(familySubmission.get());
            } else {
                log.warn(
                        "Could not find family submission for family submission id {} and provider submission {}. Not sending an unidentified provider email to the family.",
                        familySubmissionId.get(), submission.getId());
            }
        } else {
            log.warn(
                    "Could not find family submission id for provider submission {}. Not sending an unidentified provider email to the family.",
                    submission.getId());
        }

        sendUnidentifiedProviderConfirmationEmail.send(submission);
    }
}
