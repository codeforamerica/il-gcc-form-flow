package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProviderResponseHasExpired implements Condition {
  SubmissionRepositoryService submissionRepositoryService;

  public ProviderResponseHasExpired(SubmissionRepositoryService submissionRepositoryService) {
      this.submissionRepositoryService = submissionRepositoryService;
  }

  @Override
  public Boolean run(Submission providerSubmission) {
    Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
    if (familySubmissionId.isPresent()) {
      Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId.get());
      if (familySubmissionOptional.isPresent()) {
        Submission familySubmission = familySubmissionOptional.get();
        String providerSubmissionStatus = (ProviderSubmissionUtilities.getOneProviderApplicationResponseStatus(
            familySubmission, providerSubmission));
        return providerSubmissionStatus.equalsIgnoreCase(SubmissionStatus.EXPIRED.toString());
        }
      }
    return false;
  }
}
