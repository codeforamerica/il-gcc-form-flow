package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.ACTIVE_SITE_ADMINISTERED_PROVIDER;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.ACTIVE_SITE_ADMIN_RESOURCE_ORG;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.CURRENT_APPROVED_PROVIDER;
import static org.mockito.Mockito.when;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.ilgcc.app.submission.router.ApplicationRoutingServiceImpl;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class SetResourceOrganizationTest {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SetResourceOrganization setResourceOrganization;
    
    @MockitoBean
    ApplicationRoutingServiceImpl applicationRouterServiceImpl;

    Submission familySubmission;
    Submission providerSubmission;

    private FormSubmission formSubmission;

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


        when(applicationRouterServiceImpl.getSiteAdministeredOrganizationByProviderId(ACTIVE_SITE_ADMINISTERED_PROVIDER.getProviderId()))
                .thenReturn(Optional.of(ACTIVE_SITE_ADMIN_RESOURCE_ORG));

        submissionRepositoryService.save(providerSubmission);
        
        Map<String, Object> formData = new HashMap<>();
        formData.put("providerResponseAgreeToCare", true);
        formSubmission = new FormSubmission(formData);
    }

    @Test
    void shouldChangeFamilyOrgIdWhenProviderIsSiteAdministered() {
        setResourceOrganization.run(formSubmission, providerSubmission);
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
        setResourceOrganization.run(formSubmission, providerSubmission);
        familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();
        assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo("testValue");
        assertThat(familySubmission.getInputData().get("ccrrName")).isEqualTo("CCRR Name");
        assertThat(familySubmission.getInputData().get("ccrrPhoneNumber")).isEqualTo("(123) 123-1234");
    }

    @Nested
    class MultiProviderTests {
        private Map<String, Object> provider1 = new HashMap<>();
        private Map<String, Object> provider2 = new HashMap<>();

        private Map<String, Object> child1 = new HashMap<>();
        private Map<String, Object> child2 = new HashMap<>();
        private Map<String, Object> child3 = new HashMap<>();

        @BeforeEach
        void addProvidersToFamilySubmission() {
            provider1.put("uuid", UUID.randomUUID().toString());
            provider1.put("providerName", "First Provider");
            provider1.put("providerResourceOrgId", "10101");
            provider1.put("providerResponseAgreeToCare", true);

            provider2.put("uuid", UUID.randomUUID().toString());
            provider2.put("providerName", "Second Provider");
            provider2.put("providerResourceOrgId", "10101");
            provider2.put("providerResponseAgreeToCare", true);

            child1.put("uuid", UUID.randomUUID().toString());
            child1.put("childFirstName", "First");
            child1.put("childLastName", "Child");
            child1.put("childInCare", "true");
            child1.put("childDateOfBirthMonth", "10");
            child1.put("childDateOfBirthDay", "11");
            child1.put("childDateOfBirthYear", "2020");
            child1.put("needFinancialAssistanceForChild", true);
            child1.put("childIsUsCitizen", "Yes");
            child1.put("ccapStartDate", "01/10/2025");

            child2.put("uuid", UUID.randomUUID().toString());
            child2.put("childFirstName", "Second");
            child2.put("childLastName", "Child");
            child2.put("childInCare", "true");
            child2.put("childDateOfBirthMonth", "12");
            child2.put("childDateOfBirthDay", "11");
            child2.put("childDateOfBirthYear", "2021");
            child2.put("needFinancialAssistanceForChild", true);
            child2.put("childIsUsCitizen", "Yes");
            child2.put("ccapStartDate", "12/10/2025");

            child3.put("uuid", UUID.randomUUID().toString());
            child3.put("childFirstName", "Third");
            child3.put("childLastName", "Child");
            child3.put("childInCare", "true");
            child3.put("childDateOfBirthMonth", "10");
            child3.put("childDateOfBirthDay", "10");
            child3.put("childDateOfBirthYear", "2029");
            child3.put("needFinancialAssistanceForChild", true);
            child3.put("childIsUsCitizen", "Yes");
            child3.put("ccapStartDate", "12/10/2024");

            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", new ArrayList<>(List.of(provider1, provider2)))
                    .with("children", new ArrayList<>(List.of(child1, child2)))
                    .withMultipleChildcareSchedules(List.of(child1.get("uuid").toString(), child2.get("uuid").toString()),
                            List.of(provider1.get("uuid").toString(), provider2.get("uuid").toString()))
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withShortCode("testShortCode")
                    .build());

            Map<String, Object> familyInputData = familySubmission.getInputData();
            familyInputData.put("organizationId", "testValue");
            familyInputData.put("ccrrName", "CCRR Name");
            familyInputData.put("ccrrPhoneNumber", "(123) 123-1234");
            familyInputData.put("familyIntendedProviderName", "Test Provider");
            submissionRepositoryService.save(familySubmission);
            providerSubmission.getInputData().put("familySubmissionId", familySubmission.getId().toString());
            submissionRepositoryService.save(providerSubmission);

            Map<String, Object> formData = new HashMap<>();
            formData.put("providerResponseAgreeToCare", true);
            formSubmission = new FormSubmission(formData);
        }

        @Test
        void shouldChangeFamilyOrgIdWhenAllProvidersHaveTheSameOrgId() {
            providerSubmission.getInputData().put("currentProviderUuid", provider2.get("uuid").toString());
            submissionRepositoryService.save(providerSubmission);
            setResourceOrganization.run(formSubmission, providerSubmission);
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
            provider3.put("providerResourceOrgId", "someOtherOrgId");
            BigInteger providerId = new BigInteger(providerSubmission.getInputData().get("providerResponseProviderNumber").toString());
            when(applicationRouterServiceImpl.getSiteAdministeredOrganizationByProviderId(providerId))
                    .thenReturn(Optional.of(ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA));

            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", new ArrayList<>(List.of(provider1, provider2, provider3)))
                    .with("children", new ArrayList<>(List.of(child1, child2, child3)))
                    .withMultipleChildcareSchedules(List.of(child1.get("uuid").toString(), child2.get("uuid").toString(), child3.get("uuid").toString()),
                            List.of(provider1.get("uuid").toString(), provider2.get("uuid").toString(), provider3.get("uuid").toString()))
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withShortCode("testShortCode")
                    .build());
            Map<String, Object> familyInputData = familySubmission.getInputData();
            familyInputData.put("organizationId", "testValue");
            familyInputData.put("ccrrName", "CCRR Name");
            familyInputData.put("ccrrPhoneNumber", "(123) 123-1234");
            familyInputData.put("familyIntendedProviderName", "Test Provider");
            providerSubmission.getInputData().put("familySubmissionId", familySubmission.getId().toString());
            providerSubmission.getInputData().put("currentProviderUuid", provider3.get("uuid"));
            submissionRepositoryService.save(providerSubmission);
            submissionRepositoryService.save(familySubmission);

            setResourceOrganization.run(formSubmission, providerSubmission);
            familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();

            assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo("testValue");
            assertThat(familySubmission.getInputData().get("ccrrName")).isEqualTo("CCRR Name");
            assertThat(familySubmission.getInputData().get("ccrrPhoneNumber")).isEqualTo("(123) 123-1234");
        }

        @Test
        void shouldNotChangeFamilyOrgIdWhenAllProvidersDoNotHaveAChildCareSchedule() {
            Map<String, Object> provider3 = new HashMap<>();
            provider3.put("uuid", UUID.randomUUID().toString());
            provider3.put("providerName", "Third Provider");
            provider3.put("providerResourceOrgId", "someOtherOrgId");
            BigInteger providerId = new BigInteger(providerSubmission.getInputData().get("providerResponseProviderNumber").toString());
            when(applicationRouterServiceImpl.getSiteAdministeredOrganizationByProviderId(providerId))
                    .thenReturn(Optional.of(ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA));

            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .with("parentFirstName", "FirstName")
                    .with("parentContactEmail", "familyemail@test.com")
                    .with("languageRead", "English")
                    .with("providers", new ArrayList<>(List.of(provider1, provider2, provider3)))
                    .with("children", new ArrayList<>(List.of(child1, child2, child3)))
                    .withMultipleChildcareSchedules(List.of(child1.get("uuid").toString(), child2.get("uuid").toString()),
                            List.of(provider1.get("uuid").toString(), provider2.get("uuid").toString(), provider3.get("uuid").toString()))
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withShortCode("testShortCode")
                    .build());
            Map<String, Object> familyInputData = familySubmission.getInputData();
            familyInputData.put("organizationId", "testValue");
            familyInputData.put("ccrrName", "CCRR Name");
            familyInputData.put("ccrrPhoneNumber", "(123) 123-1234");
            familyInputData.put("familyIntendedProviderName", "Test Provider");

            providerSubmission.getInputData().put("familySubmissionId", familySubmission.getId().toString());
            providerSubmission.getInputData().put("currentProviderUuid", provider1.get("uuid"));
            submissionRepositoryService.save(providerSubmission);
            submissionRepositoryService.save(familySubmission);

            setResourceOrganization.run(formSubmission, providerSubmission);
            familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();

            assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo("testValue");
            assertThat(familySubmission.getInputData().get("ccrrName")).isEqualTo("CCRR Name");
            assertThat(familySubmission.getInputData().get("ccrrPhoneNumber")).isEqualTo("(123) 123-1234");
        }
        
        @Test
        void shouldNotChangeFamilyOrgIdWhenOnlyOneProviderHasAnOrgId() {
            List<Map<String, Object>> providers = (List<Map<String, Object>>) familySubmission.getInputData().get("providers");
            providers.get(1).remove("providerResourceOrgId");
            submissionRepositoryService.save(familySubmission);
            List<Map<String, Object>> providersAfterRemovingOrgId = (List<Map<String, Object>>) familySubmission.getInputData().get("providers");
            assertThat(providersAfterRemovingOrgId.get(0).get("providerResourceOrgId")).isNotNull();
            assertThat(providersAfterRemovingOrgId.get(1).get("providerResourceOrgId")).isNull();
            providerSubmission.getInputData().put("currentProviderUuid", providersAfterRemovingOrgId.get(0).get("uuid").toString());
            submissionRepositoryService.save(providerSubmission);

            setResourceOrganization.run(formSubmission, providerSubmission);
            familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();

            assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo("testValue");
            assertThat(familySubmission.getInputData().get("ccrrName")).isEqualTo("CCRR Name");
            assertThat(familySubmission.getInputData().get("ccrrPhoneNumber")).isEqualTo("(123) 123-1234");
        }
        
        @Test
        void shouldNotChangeFamilyOrgIdWhenProviderDoesNotAgreeToCare() {
            formSubmission.getFormData().put("providerResponseAgreeToCare", false);
            providerSubmission.getInputData().put("currentProviderUuid", provider2.get("uuid").toString());
            submissionRepositoryService.save(providerSubmission);
            setResourceOrganization.run(formSubmission, providerSubmission);
            familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();

            assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo("testValue");
            assertThat(familySubmission.getInputData().get("ccrrName")).isEqualTo("CCRR Name");
            assertThat(familySubmission.getInputData().get("ccrrPhoneNumber")).isEqualTo("(123) 123-1234");
        }
        
        @Test
        void shouldNotChangeFamilyOrgIdWhenAProviderHasNotYetResponded() {
            Map<String, Object> formData = new HashMap<>();
            formData.put("providerResponseAgreeToCare", false);
            formSubmission = new FormSubmission(formData);
            providerSubmission.getInputData().put("currentProviderUuid", provider2.get("uuid").toString());
            submissionRepositoryService.save(providerSubmission);
            List<Map<String, Object>> providers = (List<Map<String, Object>>) familySubmission.getInputData().get("providers");
            providers.get(1).remove("providerResponseAgreeToCare");
            familySubmission.getInputData().put("providers", providers);
            submissionRepositoryService.save(familySubmission);
            
            setResourceOrganization.run(formSubmission, providerSubmission);
            familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();
            assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo("testValue");
            assertThat(familySubmission.getInputData().get("ccrrName")).isEqualTo("CCRR Name");
            assertThat(familySubmission.getInputData().get("ccrrPhoneNumber")).isEqualTo("(123) 123-1234");
        }
    }
}