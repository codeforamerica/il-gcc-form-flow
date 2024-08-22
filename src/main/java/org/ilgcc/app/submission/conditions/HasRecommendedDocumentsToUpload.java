package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.ilgcc.app.utils.RecommendedDocumentsUtilities;
import org.springframework.stereotype.Component;

@Component
public class HasRecommendedDocumentsToUpload implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return RecommendedDocumentsUtilities.shouldDisplayRecommendedDocumentScreens(submission);
  }

}
