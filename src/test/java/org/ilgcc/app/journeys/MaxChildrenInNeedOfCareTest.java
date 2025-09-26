package org.ilgcc.app.journeys;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import formflow.library.data.Submission;
import java.util.Optional;
import java.util.UUID;
import org.ilgcc.app.utils.AbstractMockMvcTest;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

public class MaxChildrenInNeedOfCareTest extends AbstractMockMvcTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();

        UUID submissionId = UUID.randomUUID();

        // Build a submission with 5 children needing financial assistance
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("childcareSchedules", emptyList())
                .withChild("First", "Child", "true")
                .withChild("Second", "Child", "true")
                .withChild("Third", "Child", "true")
                .withChild("Fourth", "Child", "true")
                .withChild("Fifth", "Child", "true")
                .build();

        // Make the repository return our in-memory submission
        when(submissionRepositoryService.findById(submissionId))
                .thenReturn(Optional.of(submission));

        // Tie the session to this submission for the "gcc" flow
        setFlowInfoInSession(session, "gcc", submissionId);
    }

    @Test
    void schedulesReview_showsNotice_whenFiveOrMoreChildrenNeedCare() throws Exception {
        String url = getUrlForPageName("schedules-review");

        ResultActions resp = getFromUrl(getUrlForPageName("schedules-review"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"more-than-4-children-needing-care-notice\"")))
                .andExpect(content().string(containsString("Child care schedules can only be added for 4 children.")))
                .andExpect(content().string(containsString("Your other children will be included on the application with limited care details.")));
    }
}
