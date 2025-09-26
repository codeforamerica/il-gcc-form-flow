package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class RemoveIncompleteChildIterationsTest {

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    RemoveIncompleteChildIterations action;

    Map<String, Object> child1 = new HashMap<>();
    Map<String, Object> child2 = new HashMap<>();
    Submission familySubmission;

    @AfterEach
    public void tearDown() {
        submissionRepository.deleteAll();
    }

    // ToDo: remove as part of EnableMultipleProviders cleanup?
    @Nested
    class updateEarliestCCAPStartDate {

        @Test
        public void removesEarliestDateWhenNoChildren() {
            familySubmission = new SubmissionTestBuilder().build();
            action.run(familySubmission);

            assertThat(familySubmission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("");
        }

        @Test
        public void setsEarliestDateWithMultipleChildren() {
            familySubmission = new SubmissionTestBuilder()
                    .withChild("first", "child", "true")
                    .withChild("second", "child", "true")
                    .withChild("third", "child", "true")
                    .addChildCareStartDate(0, "2009", "2", "10")
                    .addChildCareStartDate(1, "2009", "1", "10")
                    .addChildCareStartDate(2, "2020", "1", "10")
                    .build();

            action.run(familySubmission);

            assertThat(familySubmission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("01/10/2009");
        }

        @Test
        public void setsEarliestDateWithSingleChild() {
            familySubmission = new SubmissionTestBuilder()
                    .withChild("first", "child", "true")
                    .addChildCareStartDate(0, "2020", "2", "10")
                    .build();

            action.run(familySubmission);

            assertThat(familySubmission.getInputData().get("earliestChildcareStartDate").toString()).isEqualTo("02/10/2020");
        }
    }

    @Nested
    class run {

        @BeforeEach
        void setUp() {
            Map<String, Object> provider = new HashMap<>();
            provider.put("uuid", "first-provider-uuid");
            provider.put("iterationIsComplete", true);
            provider.put("childCareProgramName", "FamilyChildCareName");
            provider.put("providerType", "Individual");

            child1.put("uuid", "child-1-uuid");
            child1.put("childFirstName", "First");
            child1.put("childLastName", "Child");
            child1.put("childDateOfBirthMonth", "10");
            child1.put("childDateOfBirthDay", "11");
            child1.put("childDateOfBirthYear", "2002");
            child1.put("needFinancialAssistanceForChild", true);
            child1.put("childIsUsCitizen", "Yes");

            child2.put("uuid", "child-2-uuid");
            child2.put("childFirstName", "Second");
            child2.put("childLastName", "Child");
            child2.put("childInCare", "true");
            child2.put("childDateOfBirthMonth", "10");
            child2.put("childDateOfBirthDay", "11");
            child2.put("childDateOfBirthYear", "2002");
            child2.put("needFinancialAssistanceForChild", true);

            familySubmission = submissionRepository.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", List.of(provider))
                    .with("children", List.of(child1, child2))
                    .withMultipleChildcareSchedules(List.of("child-1-uuid", "child-2-uuid"),
                            List.of("first-provider-uuid", "first-provider-uuid"))
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withCCRR()
                    .build());
        }

        @Test
        public void removesIncompleteIterationsForChildren() {
            child1.put("iterationIsComplete", true);
            child2.put("iterationIsComplete", false);

            submissionRepository.save(familySubmission);

            action.run(familySubmission);

            List<Map<String, Object>> children = (List) familySubmission.getInputData().get("children");
            assertThat(children.size()).isEqualTo(1);
            assertThat(children.get(0).get("uuid")).isEqualTo("child-1-uuid");
        }

        @Test
        public void removesChildcareSchedulesIfPresentWhenChildrenListIsEmpty() {
            familySubmission.getInputData().remove("children");
            submissionRepository.save(familySubmission);

            action.run(familySubmission);

            assertThat(familySubmission.getInputData().containsKey("childCareSchedules")).isFalse();
        }

        @Test
        public void filtersChildcareSchedulesToContainOnlyChildrenInList() {
            child1.put("iterationIsComplete", true);
            child2.put("iterationIsComplete", true);
            familySubmission.getInputData().put("children", List.of(child2));
            submissionRepository.save(familySubmission);

            action.run(familySubmission);

            List<Map<String, Object>> children = (List) familySubmission.getInputData().get("children");
            assertThat(children.size()).isEqualTo(1);

            List<Map<String, Object>> childCareSchedules = (List) familySubmission.getInputData().get("childcareSchedules");
            assertThat(childCareSchedules.size()).isEqualTo(1);
            assertThat(childCareSchedules.get(0).get("childUuid")).isEqualTo("child-2-uuid");
        }
    }
}
