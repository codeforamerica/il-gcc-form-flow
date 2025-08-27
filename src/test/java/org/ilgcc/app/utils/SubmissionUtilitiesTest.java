package org.ilgcc.app.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SubmissionUtilitiesTest {

    @Test
    void allChildcareSchedulesAreForTheSameProviderReturnsTrueWhenAllChildcareSchedulesHaveSameProviderUuid() {
        Map<String, Object> child1  = getChild1();
        Map<String, Object> child2  = getChild2();


      SubmissionTestBuilder submissionTestBuilder = new SubmissionTestBuilder();
      submissionTestBuilder.with("children", List.of(child1, child2));
      submissionTestBuilder.withMultipleChildcareSchedulesAllBelongingToTheSameProvider(
          List.of(child1.get("uuid").toString(), child2.get("uuid").toString()), "provider-id");
      Submission submission = submissionTestBuilder.build();
        boolean result = SubmissionUtilities.allChildcareSchedulesAreForTheSameProvider(
                submission.getInputData());
        assertThat(result).isTrue();
    }

    @Test
    void allChildcareSchedulesAreForTheSameProviderReturnsFalseWhenAllChildcareSchedulesDoNotHaveSameProviderUuid() {
        Map<String, Object> child1  = getChild1();
        Map<String, Object> child2  = getChild2();
        
        Submission submission = new SubmissionTestBuilder()
                .with("children", List.of(child1, child2))
                .withMultipleChildcareSchedulesBelongingToDifferentProviders(List.of(child1.get("uuid").toString(), child2.get("uuid").toString()), List.of("p1", "p2")).build();
        boolean result = SubmissionUtilities.allChildcareSchedulesAreForTheSameProvider(submission.getInputData());
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

    @Test
    void getAllChildcareSchedulesReturnsChildcareSchedulesWithSameProviderReturnsCorrectChildcareSchedules() {

        Map<String, Object> child1  = getChild1();
        Map<String, Object> child2  = getChild2();
        Map<String, Object> child3  = getChild3();

        Submission submission = new SubmissionTestBuilder()
            .withProvider("provider1", "1")
            .withProvider("provider2", "2")
            .with("children", List.of(child1, child2, child3))
            .withMultipleChildcareSchedulesForProvider(List.of((String) child1.get("uuid"), (String) child2.get("uuid")), "provider1-1")
            .withMultipleChildcareSchedulesForProvider(List.of((String) child3.get("uuid")), "provider2-2").build();

        List<Map<String, Object>> provider1ChildcareSchedules = SubmissionUtilities.getAllChildcareSchedulesWithTheSameProvider(
            (List<Map<String, Object>>) submission.getInputData().get("childcareSchedules"), "provider1-1");
        assertThat(provider1ChildcareSchedules.size()).isEqualTo(2);

        List<Map<String, Object>> provider2ChildcareSchedules = SubmissionUtilities.getAllChildcareSchedulesWithTheSameProvider(
            (List<Map<String, Object>>) submission.getInputData().get("childcareSchedules"), "provider2-2");
        assertThat(provider2ChildcareSchedules.size()).isEqualTo(1);
    }

    @Test
    public void getNamesOfProvidersMissingChildcareSchedulesShouldReturnTheCorrectNames() {
        Map<String, Object> child1  = getChild1();
        Map<String, Object> child2  = getChild2();
        Map<String, Object> child3  = getChild3();

        Submission submission = new SubmissionTestBuilder()
            .withProvider("provider1", "1")
            .withProvider("provider2", "2")
            .withProvider("provider3", "3")
            .with("children", List.of(child1, child2, child3))
            .withMultipleChildcareSchedulesForProvider(List.of((String) child1.get("uuid"), (String) child2.get("uuid"), (String) child3.get("uuid")), "provider1-1").build();

        List<String> listOfProvidersMissingChildcareSchedules = new ArrayList<>();
        listOfProvidersMissingChildcareSchedules = SubmissionUtilities.getNamesOfProvidersMissingChildcareSchedules(submission.getInputData());
        assertThat(listOfProvidersMissingChildcareSchedules.size()).isEqualTo(2);
        assertThat(listOfProvidersMissingChildcareSchedules.contains("provider2")).isTrue();
        assertThat(listOfProvidersMissingChildcareSchedules.contains("provider3")).isTrue();
        assertThat(listOfProvidersMissingChildcareSchedules.contains("provider1")).isFalse();
    }
    @Test
    public void areAllProvidersMissingChildcareSchedulesShouldReturnTrueIfNoProvidersHaveChildcareSchedules() {
        Map<String, Object> child1  = getChild1();
        Map<String, Object> child2  = getChild2();
        Map<String, Object> child3  = getChild3();
        Submission submission = new SubmissionTestBuilder()
            .withProvider("provider1", "1")
            .withProvider("provider2", "2")
            .withProvider("provider3", "3")
            .with("children", List.of(child1, child2, child3)).build();
        assertThat(SubmissionUtilities.areAllProvidersMissingChildcareSchedules(submission.getInputData())).isTrue();
    }

    @Test
    public void areAllProvidersMissingChildcareSchedulesShouldReturnFalseIfAnyProviderHasAChildcareSchedule() {
        Map<String, Object> child1  = getChild1();
        Map<String, Object> child2  = getChild2();
        Map<String, Object> child3  = getChild3();
        Submission submission = new SubmissionTestBuilder()
            .withProvider("provider1", "1")
            .withProvider("provider2", "2")
            .withProvider("provider3", "3")
            .with("children", List.of(child1, child2, child3))
            .withMultipleChildcareSchedulesForProvider(List.of((String) child1.get("uuid")), "provider1-1")
            .build();
        assertThat(SubmissionUtilities.areAllProvidersMissingChildcareSchedules(submission.getInputData())).isFalse();
    }

    private Map<String, Object> getChild1(){
        Map<String, Object> child1 = new HashMap<>();
        child1.put("uuid", UUID.randomUUID().toString());
        child1.put("childFirstName", "First");
        child1.put("childLastName", "Child");
        child1.put("childInCare", "true");
        child1.put("childDateOfBirthMonth", "10");
        child1.put("childDateOfBirthDay", "11");
        child1.put("childDateOfBirthYear", "2002");
        child1.put("needFinancialAssistanceForChild", true);
        child1.put("childIsUsCitizen", "Yes");
        child1.put("ccapStartDate", "01/10/2025");
        return child1;
    }

    private Map<String, Object> getChild2(){
        Map<String, Object> child2 = new HashMap<>();
        child2.put("uuid", UUID.randomUUID().toString());
        child2.put("childFirstName", "Second");
        child2.put("childLastName", "Child");
        child2.put("childInCare", "true");
        child2.put("childDateOfBirthMonth", "10");
        child2.put("childDateOfBirthDay", "11");
        child2.put("childDateOfBirthYear", "2002");
        child2.put("needFinancialAssistanceForChild", true);
        child2.put("childIsUsCitizen", "Yes");
        child2.put("ccapStartDate", "01/10/2025");
        return child2;
    }

    private Map<String, Object> getChild3(){
        Map<String, Object> child3 = new HashMap<>();
        child3.put("uuid", UUID.randomUUID().toString());
        child3.put("childFirstName", "Thrid");
        child3.put("childLastName", "Kid");
        child3.put("childInCare", "true");
        child3.put("childDateOfBirthMonth", "12");
        child3.put("childDateOfBirthDay", "11");
        child3.put("childDateOfBirthYear", "2020");
        child3.put("needFinancialAssistanceForChild", true);
        child3.put("childIsUsCitizen", "Yes");
        child3.put("ccapStartDate", "12/03/2023");
        return child3;
    }
}


    @Test
    void isPreMultiProviderApplicationWithSingleProviderShouldReturnFalseIfIsMultiproviderApplication() {
        Submission submission = new SubmissionTestBuilder().withMultipleChildcareSchedules(List.of("C1", "C2"), List.of("P1", "P2")).build();
        boolean result = SubmissionUtilities.isPreMultiProviderApplicationWithSingleProvider(submission);
        assertThat(result).isFalse();
    }
}