package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProviderResponseIsBeforeThreeDayWindow implements Condition {
  SubmissionRepositoryService submissionRepositoryService;
  ProviderResponseIsBeforeThreeDayWindow(SubmissionRepositoryService submissionRepositoryService) {
    this.submissionRepositoryService = submissionRepositoryService;
  }
  @Override
  public Boolean run(Submission providerSubmission) {
    ProviderResponseIsAfterThreeDayWindow providerResponseIsAfterThreeDayWindow = new ProviderResponseIsAfterThreeDayWindow(submissionRepositoryService);
    return !providerResponseIsAfterThreeDayWindow.run(providerSubmission);
  }
}
