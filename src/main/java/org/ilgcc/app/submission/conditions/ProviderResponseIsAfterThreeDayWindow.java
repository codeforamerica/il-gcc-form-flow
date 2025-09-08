package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;

@Slf4j
public class ProviderResponseIsAfterThreeDayWindow implements Condition {
  SubmissionRepositoryService submissionRepositoryService;

  public ProviderResponseIsAfterThreeDayWindow(SubmissionRepositoryService submissionRepositoryService) {
      this.submissionRepositoryService = submissionRepositoryService;
  }

  @Override
  public Boolean run(Submission providerSubmission){
    Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
    if (familySubmissionId.isPresent()) {
      Optional<Submission> familySubmissionOptional = submissionRepositoryService.findById(familySubmissionId.get());
      AtomicBoolean familySubmissionIsAfterThreeDays = new AtomicBoolean(false);
      familySubmissionOptional.ifPresent(familySubmission -> {
        familySubmissionIsAfterThreeDays.set(ProviderSubmissionUtilities.providerApplicationHasExpired(familySubmission));
      });
      return familySubmissionIsAfterThreeDays.get();
    }else{
      return false;
    }
  }
}
