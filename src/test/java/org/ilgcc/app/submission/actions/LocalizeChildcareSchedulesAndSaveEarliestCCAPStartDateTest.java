package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.ilgcc.app.utils.TestUtils.createCareSchedule;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LocalizeChildcareSchedulesAndSaveEarliestCCAPStartDateTest {

    @Autowired
    SubmissionRepository submissionRepository;

    @Autowired
    LocalizeChildcareSchedulesAndSaveEarliestCCAPStartDate localizeChildcareSchedulesAndSaveEarliestCCAPStartDate;

    Submission familySubmission;

    private static Map<String, Object> provider1 = new HashMap<>();

    private static Map<String, Object> provider2 = new HashMap<>();

    private static Map<String, Object> child1 = new HashMap<>();

    private static Map<String, Object> child2 = new HashMap<>();

    private static Map<String, Object> child3 = new HashMap<>();


    @BeforeEach
    void setUp() {

        provider1.put("uuid", UUID.randomUUID().toString());
        provider1.put("iterationIsComplete", true);
        provider1.put("providerFirstName", "FirstName");
        provider1.put("providerLastName", "LastName");

        provider2.put("uuid", UUID.randomUUID().toString());
        provider2.put("iterationIsComplete", true);
        provider2.put("childCareProgramName", "Child Care Program Name");

        child1.put("uuid", UUID.randomUUID().toString());
        child1.put("childFirstName", "First");
        child1.put("childLastName", "Child");
        child1.put("needFinancialAssistanceForChild", true);

        child2.put("uuid", UUID.randomUUID().toString());
        child2.put("childFirstName", "Second");
        child2.put("childLastName", "Child");
        child2.put("needFinancialAssistanceForChild", true);

        child3.put("uuid", UUID.randomUUID().toString());
        child3.put("childFirstName", "Third");
        child3.put("childLastName", "Child");
        child3.put("needFinancialAssistanceForChild", true);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withParentBasicInfo()
                .with("providers", List.of(provider1, provider2))
                .with("children", List.of(child1, child2, child3))
                .build();
    }

    @AfterEach
    void tearDown() {
        submissionRepository.deleteAll();
    }

    @Test
    void setsChildCareScheduleLocalized() {
        familySubmission = new SubmissionTestBuilder(familySubmission)
                .withMultipleChildcareSchedulesForProvider(
                        List.of(child1.get("uuid").toString(), child2.get("uuid").toString()),
                        provider1.get("uuid").toString())
                .build();

        localizeChildcareSchedulesAndSaveEarliestCCAPStartDate.run(familySubmission);

        List<Map<String, Object>> childcareSchedulesData = (List<Map<String, Object>>) familySubmission.getInputData()
                .get("childcareSchedules");

        for (Map<String, Object> childCareSchedule : childcareSchedulesData) {
            List<Map<String, Object>> providerSchedulesData = (List<Map<String, Object>>) childCareSchedule.get(
                    "providerSchedules");

            for (Map<String, Object> providerSchedule : providerSchedulesData) {
                assertThat(providerSchedule.containsKey("childCareScheduleLocalized")).isTrue();
            }
        }
    }

    @Test
    void setsCorrectEarliestStartDateWithMultipleProvidersAndCCAPStartDate() {
        List<Map<String, Object>> childcareSchedulesData = new ArrayList<>();

        childcareSchedulesData.add(Map.of("childUuid", child1.get("uuid").toString(), "providerSchedules",
                List.of(createCareSchedule(provider1.get("uuid").toString(), child1.get("uuid").toString(), "01/10/2022"),
                        createCareSchedule(provider2.get("uuid").toString(), child1.get("uuid").toString(), "01/10/2027"))));

        childcareSchedulesData.add(Map.of("childUuid", child2.get("uuid").toString(), "providerSchedules",
                List.of(createCareSchedule(provider1.get("uuid").toString(), child2.get("uuid").toString(), "02/10/2022"),
                        createCareSchedule(provider2.get("uuid").toString(), child2.get("uuid").toString(), "01/10/2026"))));

        childcareSchedulesData.add(Map.of("childUuid", child3.get("uuid").toString(), "providerSchedules",
                List.of(createCareSchedule(provider1.get("uuid").toString(), child3.get("uuid").toString(), "02/10/2020"))));

        familySubmission.getInputData().put("childcareSchedules", childcareSchedulesData);

        localizeChildcareSchedulesAndSaveEarliestCCAPStartDate.run(familySubmission);

        assertThat(provider1.get("earliestChildcareStartDate")).isEqualTo("02/10/2020");
        assertThat(provider2.get("earliestChildcareStartDate")).isEqualTo("01/10/2026");
    }

    @Test
    void setsCorrectEarliestStartDateWithMultipleProvidersAndNoCCAPStartDate() {
        List<Map<String, Object>> childcareSchedulesData = new ArrayList<>();

        childcareSchedulesData.add(Map.of("childUuid", child1.get("uuid").toString(), "providerSchedules",
                List.of(createCareSchedule(provider1.get("uuid").toString(), child1.get("uuid").toString(), ""),
                        createCareSchedule(provider2.get("uuid").toString(), child1.get("uuid").toString(), ""))));

        childcareSchedulesData.add(Map.of("childUuid", child2.get("uuid").toString(), "providerSchedules",
                List.of(createCareSchedule(provider1.get("uuid").toString(), child2.get("uuid").toString(), ""),
                        createCareSchedule(provider2.get("uuid").toString(), child2.get("uuid").toString(), ""))));

        childcareSchedulesData.add(Map.of("childUuid", child3.get("uuid").toString(), "providerSchedules",
                List.of(createCareSchedule(provider1.get("uuid").toString(), child3.get("uuid").toString(), ""))));

        familySubmission.getInputData().put("childcareSchedules", childcareSchedulesData);

        localizeChildcareSchedulesAndSaveEarliestCCAPStartDate.run(familySubmission);

        assertThat(provider1.get("earliestChildcareStartDate")).isEqualTo("");
        assertThat(provider2.get("earliestChildcareStartDate")).isEqualTo("");
    }

    @Test
    void setsCorrectEarliestStartDateWithMultipleProvidersAndSomeCCAPStartDate() {
        List<Map<String, Object>> childcareSchedulesData = new ArrayList<>();

        childcareSchedulesData.add(Map.of("childUuid", child1.get("uuid").toString(), "providerSchedules",
                List.of(createCareSchedule(provider1.get("uuid").toString(), child1.get("uuid").toString(), "01/10/2022"),
                        createCareSchedule(provider2.get("uuid").toString(), child1.get("uuid").toString(), "01/10/2027"))));

        childcareSchedulesData.add(Map.of("childUuid", child2.get("uuid").toString(), "providerSchedules",
                List.of(createCareSchedule(provider1.get("uuid").toString(), child2.get("uuid").toString(), "02/10/2022"),
                        createCareSchedule(provider2.get("uuid").toString(), child2.get("uuid").toString(), ""))));

        childcareSchedulesData.add(Map.of("childUuid", child3.get("uuid").toString(), "providerSchedules",
                List.of(createCareSchedule(provider1.get("uuid").toString(), child3.get("uuid").toString(), ""))));

        familySubmission.getInputData().put("childcareSchedules", childcareSchedulesData);

        localizeChildcareSchedulesAndSaveEarliestCCAPStartDate.run(familySubmission);

        assertThat(provider1.get("earliestChildcareStartDate")).isEqualTo("01/10/2022");
        assertThat(provider2.get("earliestChildcareStartDate")).isEqualTo("01/10/2027");
    }
}