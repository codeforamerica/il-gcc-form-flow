package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.ACTIVE_SITE_ADMINISTERED_PROVIDER;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.ACTIVE_SITE_ADMIN_RESOURCE_ORG;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.CURRENT_APPROVED_PROVIDER;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.ilgcc.app.data.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
class SetResourceOrganizationTest {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SetResourceOrganization setResourceOrganization;

    @Autowired
    ProviderRepository providerRepository;

    Submission familySubmission;
    Submission providerSubmission;

    @BeforeEach
    void setUp() {
        Map<String, Object> familyInputData = new HashMap<>();
        familyInputData.put("organizationId", "testValue");
        familyInputData.put("ccrrName", "CCRR Name");
        familyInputData.put("ccrrPhoneNumber", "(123) 123-1234");
        familyInputData.put("familyIntendedProviderName", "Test Provider");
        
        familySubmission = Submission.builder()
                .inputData(familyInputData)
                .shortCode("testShortCode")
                .build();
        submissionRepositoryService.save(familySubmission);
        HashMap<String, Object> providerSubmissionData = new HashMap<>();
        providerSubmissionData.put("providerResponseProviderNumber",
                ACTIVE_SITE_ADMINISTERED_PROVIDER.getProviderId().toString());
        providerSubmissionData.put("familySubmissionId", familySubmission.getId().toString());
        providerSubmission = Submission.builder()
                .inputData(providerSubmissionData)
                .build();

        submissionRepositoryService.save(providerSubmission);
    }

    @Test
    void shouldChangeFamilyOrgIdWhenProviderIsSiteAdministered() {
        setResourceOrganization.run(providerSubmission);
        familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();
        assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo(
                ACTIVE_SITE_ADMIN_RESOURCE_ORG.getResourceOrgId().intValue());
        assertThat(familySubmission.getInputData().get("ccrrName")).isEqualTo(ACTIVE_SITE_ADMIN_RESOURCE_ORG.getName());
        assertThat(familySubmission.getInputData().get("ccrrPhoneNumber")).isEqualTo(ACTIVE_SITE_ADMIN_RESOURCE_ORG.getPhone());
    }

    @Test
    void shouldNotChangeFamilyOrgIdWhenProviderIsNotSiteAdministered() {
        providerSubmission.getInputData()
                .put("providerResponseProviderNumber", CURRENT_APPROVED_PROVIDER.getProviderId().toString());
        submissionRepositoryService.save(providerSubmission);
        setResourceOrganization.run(providerSubmission);
        familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();
        assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo("testValue");
        assertThat(familySubmission.getInputData().get("ccrrName")).isEqualTo("CCRR Name");
        assertThat(familySubmission.getInputData().get("ccrrPhoneNumber")).isEqualTo("(123) 123-1234");
    }

    @Nested
    class MultiProviderTests {

        @BeforeEach
        void addProvidersToFamilySubmission() {
            Map<String, Object> provider1 = new HashMap<>();
            Map<String, Object> provider2 = new HashMap<>();

            provider1.put("uuid", UUID.randomUUID().toString());
            provider1.put("providerName", "First Provider");
            provider1.put("providerResourceOrgId", "orgID");

            provider2.put("uuid", UUID.randomUUID().toString());
            provider2.put("providerName", "Second Provider");
            provider2.put("providerResourceOrgId", "orgID");

            familySubmission.getInputData().put("providers", new ArrayList<>(List.of(provider1, provider2)));
            submissionRepositoryService.save(familySubmission);
        }

        @Test
        void shouldChangeFamilyOrgIdWhenAllProvidersHaveTheSameOrgId() {
            setResourceOrganization.run(providerSubmission);
            familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();

            assertThat(familySubmission.getInputData().get("organizationId"))
                    .isEqualTo(ACTIVE_SITE_ADMIN_RESOURCE_ORG.getResourceOrgId().intValue());
            assertThat(familySubmission.getInputData().get("ccrrName"))
                    .isEqualTo(ACTIVE_SITE_ADMIN_RESOURCE_ORG.getName());
            assertThat(familySubmission.getInputData().get("ccrrPhoneNumber"))
                    .isEqualTo(ACTIVE_SITE_ADMIN_RESOURCE_ORG.getPhone());
        }

        @Test
        void shouldNotChangeFamilyOrgIdWhenAllProvidersDoNotHaveTheSameOrgId() {
            Map<String, Object> provider3 = new HashMap<>();

            provider3.put("uuid", UUID.randomUUID().toString());
            provider3.put("providerName", "Third Provider");
            provider3.put("providerResourceOrgId", "someOtherOrdId");

            familySubmission.getInputData().get("providers");
            ((List<Map<String, Object>>) familySubmission.getInputData().get("providers")).add(provider3);

            submissionRepositoryService.save(familySubmission);

            setResourceOrganization.run(providerSubmission);
            familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();

            assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo("testValue");
            assertThat(familySubmission.getInputData().get("ccrrName")).isEqualTo("CCRR Name");
            assertThat(familySubmission.getInputData().get("ccrrPhoneNumber")).isEqualTo("(123) 123-1234");
        }
    }
}