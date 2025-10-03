package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SetProvidersAndMaxProvidersWhileRemovingIncompleteIterationsTest {

    SubmissionRepositoryService repository = Mockito.mock(SubmissionRepositoryService.class);
    SetProvidersAndMaxProvidersWhileRemovingIncompleteIterations action = new SetProvidersAndMaxProvidersWhileRemovingIncompleteIterations(
            repository);

    @Test
    void shouldSetMaxProvidersAllowedToThree() {
        Submission submission = new SubmissionTestBuilder()
                .withProvider("FirstProvider", "1")
                .withProvider("SecondProvider", "2")
                .withProvider("ThirdProvider", "3")
                .with("choseProviderForEveryChildInNeedOfCare", "true").build();
        action.run(submission);
        assertThat(submission.getInputData().get("maxProvidersAllowed")).isEqualTo(3);
        assertThat(((List<?>) submission.getInputData().get("providers")).size()).isEqualTo(3);
    }

    @Test
    void shouldSetMaxProvidersAllowedToTwo() {
        Submission submission = new SubmissionTestBuilder()
                .withProvider("FirstProvider", "1")
                .withProvider("SecondProvider", "2")
                .with("choseProviderForEveryChildInNeedOfCare", "false").build();
        action.run(submission);
        assertThat(submission.getInputData().get("maxProvidersAllowed")).isEqualTo(2);
        assertThat(((List<?>) submission.getInputData().get("providers")).size()).isEqualTo(2);
    }

    @Test
    void testCleanupOfChildcareSchedulesWhenProvidersAreRemoved() {
        Map<String, Object> provider = new HashMap<>();
        provider.put("uuid", "first-provider-uuid");
        provider.put("iterationIsComplete", true);
        provider.put("childCareProgramName", "FamilyChildCareName");
        provider.put("providerType", "Individual");

        Map<String, Object> provider2 = new HashMap<>();
        provider2.put("uuid", "second-provider-uuid");
        provider2.put("iterationIsComplete", true);
        provider2.put("childCareProgramName", "FamilyChildCareName2");
        provider2.put("providerType", "Individual");

        Map<String, Object> child1 = new HashMap<>();
        child1.put("uuid", "child-1-uuid");
        child1.put("childFirstName", "First");
        child1.put("childLastName", "Child");
        child1.put("childDateOfBirthMonth", "10");
        child1.put("childDateOfBirthDay", "11");
        child1.put("childDateOfBirthYear", "2002");
        child1.put("needFinancialAssistanceForChild", true);
        child1.put("childIsUsCitizen", "Yes");

        Map<String, Object> child2 = new HashMap<>();
        child2.put("uuid", "child-2-uuid");
        child2.put("childFirstName", "Second");
        child2.put("childLastName", "Child");
        child2.put("childInCare", "true");
        child2.put("childDateOfBirthMonth", "10");
        child2.put("childDateOfBirthDay", "11");
        child2.put("childDateOfBirthYear", "2002");
        child2.put("needFinancialAssistanceForChild", true);

        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentFirstName", "FirstName")
                .with("parentContactEmail", "familyemail@test.com")
                .with("languageRead", "English")
                .with("providers", List.of(provider, provider2))
                .with("children", List.of(child1, child2))
                .withMultipleChildcareSchedules(List.of("child-1-uuid", "child-2-uuid"),
                        List.of("first-provider-uuid", "first-provider-uuid"))
                .withSubmittedAtDate(OffsetDateTime.now())
                .withCCRR()
                .build();

        assertThat(((List<?>) submission.getInputData().get("providers")).size()).isEqualTo(2);
        assertThat(((List<?>) submission.getInputData().get("childcareSchedules")).size()).isEqualTo(2);

        action.run(submission);

        // Confirm action didn't change anything, as expected because while only 1 provider is assigned, that doesn't mean we needed to remove
        // anything in the action
        assertThat(((List<?>) submission.getInputData().get("providers")).size()).isEqualTo(2);
        assertThat(((List<?>) submission.getInputData().get("childcareSchedules")).size()).isEqualTo(2);

        // Remove provider2, who is not on any schedules
        submission.getInputData().replace("providers", List.of(provider));

        // Confirm that the providers are now only 1, but there's still 2 schedules
        assertThat(((List<?>) submission.getInputData().get("providers")).size()).isEqualTo(1);
        assertThat(((List<?>) submission.getInputData().get("childcareSchedules")).size()).isEqualTo(2);

        action.run(submission);

        // Confirm action didn't change anything, as expected because while only 1 provider is assigned, that doesn't mean we needed to remove
        // anything in the action because the deleted provider was not assigned
        assertThat(((List<?>) submission.getInputData().get("providers")).size()).isEqualTo(1);
        assertThat(((List<?>) submission.getInputData().get("childcareSchedules")).size()).isEqualTo(2);

        // Re-add provider2
        submission.getInputData().replace("providers", List.of(provider, provider2));

        // Confirm we're back in the starting state
        assertThat(((List<?>) submission.getInputData().get("providers")).size()).isEqualTo(2);
        assertThat(((List<?>) submission.getInputData().get("childcareSchedules")).size()).isEqualTo(2);

        action.run(submission);

        // Confirm action didn't change anything, as expected because while only 1 provider is assigned, that doesn't mean we needed to remove
        // anything in the action
        assertThat(((List<?>) submission.getInputData().get("providers")).size()).isEqualTo(2);
        assertThat(((List<?>) submission.getInputData().get("childcareSchedules")).size()).isEqualTo(2);

        // Remove provider, who is on the schedules!
        submission.getInputData().replace("providers", List.of(provider2));

        assertThat(((List<?>) submission.getInputData().get("providers")).size()).isEqualTo(1);
        assertThat(((List<?>) submission.getInputData().get("childcareSchedules")).size()).isEqualTo(2);

        action.run(submission);

        // Confirm action removed both schedules
        assertThat(((List<?>) submission.getInputData().get("providers")).size()).isEqualTo(1);
        assertThat(((List<?>) submission.getInputData().get("childcareSchedules")).size()).isEqualTo(0);
    }
}