package org.ilgcc.app.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import org.junit.jupiter.api.Test;

class ProviderSubmissionUtilitiesTest {

    @Test
    void formatChildNamesAsCommaSeperatedList() {
        Submission singleChildName = new SubmissionTestBuilder().withChild("John", "Doe", "Yes").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeperatedList(singleChildName)).isEqualTo(
                "John Doe");

        Submission twoChildren = new SubmissionTestBuilder().withChild("John", "Doe", "Yes")
                .withChild("Jane", "Doe", "Yes").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeperatedList(twoChildren)).isEqualTo(
                "John Doe and Jane Doe");

        Submission threeChildren = new SubmissionTestBuilder().withChild("John", "Doe", "Yes")
                .withChild("Jane", "Doe", "Yes")
                .withChild("June", "Doe", "Yes").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsCommaSeperatedList(threeChildren)).isEqualTo(
                "John Doe, Jane Doe and June Doe");
    }
}