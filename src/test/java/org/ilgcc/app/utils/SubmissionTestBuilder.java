package org.ilgcc.app.utils;

import formflow.library.data.Submission;

public class SubmissionTestBuilder {

  private final Submission submission = new Submission();

  public Submission build() {
    return submission;
  }


  public SubmissionTestBuilder with(String key, Object value) {
    submission.getInputData().put(key, value);
    return this;
  }
}
