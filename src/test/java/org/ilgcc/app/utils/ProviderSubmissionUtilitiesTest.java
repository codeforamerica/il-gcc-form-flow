package org.ilgcc.app.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import org.junit.jupiter.api.Test;

class ProviderSubmissionUtilitiesTest {

    @Test
    void formatChildNamesAsComaSeperatedList() {
        Submission singleChildName = new SubmissionTestBuilder().withChild("John", "Doe", "Yes").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsComaSeperatedList(singleChildName)).isEqualTo(
                "John Doe");

        Submission twoChildren = new SubmissionTestBuilder().withChild("John", "Doe", "Yes")
                .withChild("Jane", "Doe", "Yes").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsComaSeperatedList(twoChildren)).isEqualTo(
                "John Doe and Jane Doe");

        Submission threeChildren = new SubmissionTestBuilder().withChild("John", "Doe", "Yes")
                .withChild("Jane", "Doe", "Yes")
                .withChild("June", "Doe", "Yes").build();
        assertThat(ProviderSubmissionUtilities.formatChildNamesAsComaSeperatedList(threeChildren)).isEqualTo(
                "John Doe, Jane Doe and June Doe");
    }
}