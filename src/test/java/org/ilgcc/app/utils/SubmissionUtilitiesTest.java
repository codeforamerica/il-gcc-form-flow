package org.ilgcc.app.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SubmissionUtilitiesTest {

    @Test
    void allChildcareSchedulesAreForTheSameProviderReturnsTrueWhenAllChildcareSchedulesHaveSameProviderUuid() {
        Submission submission = new SubmissionTestBuilder()
                .withMultipleChildcareSchedulesAllBelongingToTheSameProvider(List.of("child-1", "child-2"), "provider-id").build();
        boolean result = SubmissionUtilities.allChildcareSchedulesAreForTheSameProvider(
                (List<Map<String, Object>>) submission.getInputData().get("childcareSchedules"));
        assertThat(result).isTrue();
    }

    @Test
    void allChildcareSchedulesAreForTheSameProviderReturnsFalseWhenAllChildcareSchedulesDoNotHaveSameProviderUuid() {
        Submission submission = new SubmissionTestBuilder()
                .withMultipleChildcareSchedulesBelongingToDifferentProviders(List.of("c1", "c2"), List.of("p1", "p2")).build();
        boolean result = SubmissionUtilities.allChildcareSchedulesAreForTheSameProvider(
                (List<Map<String, Object>>) submission.getInputData().get("childcareSchedules"));
        assertThat(result).isFalse();
    }

    @Test
    void isPreMultiProviderApplicationWithSingleProviderShouldReturnTrueIfApplicationUsesPreMultiProviderDataStructure() {
        Submission submission = new SubmissionTestBuilder().withFamilyIntendedProviderName("provider-name").build();
        boolean result = SubmissionUtilities.isPreMultiProviderApplicationWithSingleProvider(submission);
        assertThat(result).isTrue();
    }

    @Test
    void isPreMultiProviderApplicationWithSingleProviderShouldReturnFalseIfIsMultiproviderApplication() {
        Submission submission = new SubmissionTestBuilder().withMultipleChildcareSchedules(List.of("C1", "C2"), List.of("P1", "P2")).build();
        boolean result = SubmissionUtilities.isPreMultiProviderApplicationWithSingleProvider(submission);
        assertThat(result).isFalse();
    }
}