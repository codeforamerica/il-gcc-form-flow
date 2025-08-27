package org.ilgcc.app.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SubmissionUtilitiesTest {

    @Test
    void allChildcareSchedulesAreForTheSameProviderReturnsTrueWhenAllChildcareSchedulesHaveSameProviderUuid() {
        Map<String, Object> child1  = new HashMap<>();
        Map<String, Object> child2  = new HashMap<>();
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
        
        Submission submission = new SubmissionTestBuilder()
                .with("children", List.of(child1, child2))
                .withMultipleChildcareSchedulesAllBelongingToTheSameProvider(
                        List.of(child1.get("uuid").toString(), child2.get("uuid").toString()), "provider-id").build();
        boolean result = SubmissionUtilities.allChildcareSchedulesAreForTheSameProvider(
                submission.getInputData());
        assertThat(result).isTrue();
    }

    @Test
    void allChildcareSchedulesAreForTheSameProviderReturnsFalseWhenAllChildcareSchedulesDoNotHaveSameProviderUuid() {
        Map<String, Object> child1  = new HashMap<>();
        Map<String, Object> child2  = new HashMap<>();
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
    void allProvidersBelongToTheSameSiteAdministeredResourceOrganizationReturnsTrueWhenAllProvidersHaveSameResourceOrgId() {
        Map<String, Object> provider1  = new HashMap<>();
        Map<String, Object> provider2  = new HashMap<>();
        provider1.put("uuid", UUID.randomUUID().toString());
        provider1.put("providerName", "First Provider");
        provider1.put("providerResourceOrgId", "orgID");

        provider2.put("uuid", UUID.randomUUID().toString());
        provider2.put("providerName", "Second Provider");
        provider2.put("providerResourceOrgId", "orgID");
        
        Submission submission = new SubmissionTestBuilder()
                .with("providers", List.of(provider1, provider2))
                .build();
        boolean result = SubmissionUtilities.allProvidersBelongToTheSameSiteAdministeredResourceOrganization(submission);
        assertThat(result).isTrue();
    }
    
    @Test
    void allProvidersBelongToTheSameSiteAdministeredResourceOrganizationReturnsFalseWhenProvidersHaveDifferentResourceOrgIds() {
        Map<String, Object> provider1 = new HashMap<>();
        Map<String, Object> provider2 = new HashMap<>();
        provider1.put("uuid", UUID.randomUUID().toString());
        provider1.put("providerName", "First Provider");
        provider1.put("providerResourceOrgId", "orgID1");

        provider2.put("uuid", UUID.randomUUID().toString());
        provider2.put("providerName", "Second Provider");
        provider2.put("providerResourceOrgId", "orgID2");

        Submission submission = new SubmissionTestBuilder()
                .with("providers", List.of(provider1, provider2))
                .build();
        boolean result = SubmissionUtilities.allProvidersBelongToTheSameSiteAdministeredResourceOrganization(submission);
        assertThat(result).isFalse();
    }
    
    @Test
    void allProvidersBelongToTheSameSiteAdministeredResourceOrganizationReturnsFalseWhenAProviderDoesNotHaveAResourceOrgId() {
        Map<String, Object> provider1 = new HashMap<>();
        Map<String, Object> provider2 = new HashMap<>();
        provider1.put("uuid", UUID.randomUUID().toString());
        provider1.put("providerName", "First Provider");
        provider1.put("providerResourceOrgId", "orgID1");

        provider2.put("uuid", UUID.randomUUID().toString());
        provider2.put("providerName", "Second Provider");
        // provider2 does not have a resource org id

        Submission submission = new SubmissionTestBuilder()
                .with("providers", List.of(provider1, provider2))
                .build();
        boolean result = SubmissionUtilities.allProvidersBelongToTheSameSiteAdministeredResourceOrganization(submission);
        assertThat(result).isFalse();
    }
    
    @Test
    void allProvidersBelongToTheSameSiteAdministeredResourceOrganizationReturnsTrueWhenThereIsOnlyOneProvider() {
        Map<String, Object> provider1 = new HashMap<>();
        provider1.put("uuid", UUID.randomUUID().toString());
        provider1.put("providerName", "First Provider");
        provider1.put("providerResourceOrgId", "orgID1");

        Submission submission = new SubmissionTestBuilder()
                .with("providers", List.of(provider1))
                .build();
        boolean result = SubmissionUtilities.allProvidersBelongToTheSameSiteAdministeredResourceOrganization(submission);
        assertThat(result).isTrue();
    }
    
    @Test
    void allProvidersBelongToTheSameSiteAdministeredResourceOrganizationReturnsFalseWhenProvidersIsEmpty() {
        Submission submission = new SubmissionTestBuilder()
                .with("providers", List.of())
                .build();
        boolean result = SubmissionUtilities.allProvidersBelongToTheSameSiteAdministeredResourceOrganization(submission);
        assertThat(result).isFalse();
    }
    
    @Test
    void setProviderResourceOrgIdCorrectlySetsTheResourceOrgIdForTheMatchingProvider() {
        Map<String, Object> provider1 = new HashMap<>();
        Map<String, Object> provider2 = new HashMap<>();
        provider1.put("uuid", "provider-uuid-1");
        provider1.put("providerName", "First Provider");

        provider2.put("uuid", "provider-uuid-2");
        provider2.put("providerName", "Second Provider");

        Submission submission = new SubmissionTestBuilder()
                .with("providers", List.of(provider1, provider2))
                .build();
        
        SubmissionUtilities.setProviderResourceOrgId(submission, "provider-uuid-2", "new-org-id");
        
        List<Map<String, Object>> providers = (List<Map<String, Object>>) submission.getInputData().get("providers");
        assertThat(providers.get(0).get("providerResourceOrgId")).isNull();
        assertThat(providers.get(1).get("providerResourceOrgId")).isEqualTo("new-org-id");
    }
    
    @Test
    void setProviderResourceOrgIdDoesNothingIfCurrentProviderPassedIsEmptyString() {
        Map<String, Object> provider1 = new HashMap<>();
        Map<String, Object> provider2 = new HashMap<>();
        provider1.put("uuid", "provider-uuid-1");
        provider1.put("providerName", "First Provider");

        provider2.put("uuid", "provider-uuid-2");
        provider2.put("providerName", "Second Provider");

        Submission submission = new SubmissionTestBuilder()
                .with("providers", List.of(provider1, provider2))
                .build();
        
        SubmissionUtilities.setProviderResourceOrgId(submission, "", "new-org-id");
        
        List<Map<String, Object>> providers = (List<Map<String, Object>>) submission.getInputData().get("providers");
        assertThat(providers.get(0).get("providerResourceOrgId")).isNull();
        assertThat(providers.get(1).get("providerResourceOrgId")).isNull();
    }
}