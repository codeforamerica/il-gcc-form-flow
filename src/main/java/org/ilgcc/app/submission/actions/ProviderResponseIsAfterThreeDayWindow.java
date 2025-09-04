package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import java.util.UUID;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;


public class ProviderResponseIsAfterThreeDayWindow implements Condition {
  private final SubmissionRepositoryService submissionRepositoryService;

  public ProviderResponseIsAfterThreeDayWindow(SubmissionRepositoryService submissionRepositoryService) {
      this.submissionRepositoryService = submissionRepositoryService;
  }

  @Override
  public Boolean run(Submission providerSubmission){
    Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
    if (familySubmissionId.isPresent()) {
      //Placeholder
      return false;
    }
    //Placeholder
    return false;
  }
}
