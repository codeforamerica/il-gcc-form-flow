package org.ilgcc.app.submission.filters;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;

class ChildrenInNeedOfChildCareCappedAt4Test {

    Submission submission;
    ChildrenInNeedOfChildCareCappedAt4 childrenInNeedOfChildCareCappedAt4 = new ChildrenInNeedOfChildCareCappedAt4();

    @Test
    void shouldReturnAListOfChildrenInNeedOfChildCare() {
        submission = new SubmissionTestBuilder()
                .withChild("First", "Child", "true")
                .withChild("Second", "Child", "false")
                .withChild("Third", "Child", "true")
                .withChild("Fourth", "Child", "false")
                .withChild("Fifth", "Child", "true")
                .build();
        List<HashMap<String, Object>> children = (List<HashMap<String, Object>>) submission.getInputData().get("children");

        assertEquals(5, children.size());
        assertEquals(3, (childrenInNeedOfChildCareCappedAt4.filter(children, submission).size()));
    }

    @Test
    void shouldLimitSizeTo4ChildrenWhoNeedCare() {
        submission = new SubmissionTestBuilder()
                .withChild("First", "Child", "true")
                .withChild("Second", "Child", "true")
                .withChild("Third", "Child", "true")
                .withChild("Fourth", "Child", "true")
                .withChild("Fifth", "Child", "true")
                .withChild("Sixth", "Child", "true")
                .build();
        List<HashMap<String, Object>> children = (List<HashMap<String, Object>>) submission.getInputData().get("children");
        assertEquals(6, children.size());
        assertEquals(4, (childrenInNeedOfChildCareCappedAt4.filter(children, submission).size()));
    }
}