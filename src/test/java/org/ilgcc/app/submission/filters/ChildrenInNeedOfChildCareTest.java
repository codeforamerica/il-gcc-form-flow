package org.ilgcc.app.submission.filters;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class ChildrenInNeedOfChildCareTest {

  Submission submission;
  ChildrenInNeedOfChildCare childrenInNeedOfChildCare;

  @Test
  void shouldReturnAListOfChildrenInNeedOfChildCare() {
    childrenInNeedOfChildCare = new ChildrenInNeedOfChildCare();
    submission = new SubmissionTestBuilder()
        .withChild("First", "Child", "true")
        .withChild("Second", "Child", "false")
        .withChild("Third", "Child", "true")
        .withChild("Fourth", "Child", "false")
        .withChild("Fifth", "Child", "true")
        .build();
    List<HashMap<String, Object>> children = (List<HashMap<String, Object>>) submission.getInputData().get("children");

    assertEquals(5, children.size());
    assertEquals(3, (childrenInNeedOfChildCare.filter(children, submission).size()));

  }
}