package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.ACTIVE_SITE_ADMINISTERED_PROVIDER;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.ACTIVE_SITE_ADMIN_RESOURCE_ORG;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.CURRENT_APPROVED_PROVIDER;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SetResourceOrganizationTest {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    SetResourceOrganization setResourceOrganization;

    Submission familySubmission;
    Submission providerSubmission;

    @BeforeEach
    void setUp() {
        familySubmission = Submission.builder()
                .inputData(Map.of("organizationId", "testValue", "ccrrName", "CCRR Name", "ccrrPhoneNumber", "(123) 123-1234"))
                .shortCode("testShortCode")
                .build();
        submissionRepositoryService.save(familySubmission);
        HashMap<String, Object> providerSubmissionData = new HashMap<>();
        providerSubmissionData.put("providerResponseProviderNumber", ACTIVE_SITE_ADMINISTERED_PROVIDER.getProviderId().toString());
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
        assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo(ACTIVE_SITE_ADMIN_RESOURCE_ORG.getResourceOrgId().intValue());
        assertThat(familySubmission.getInputData().get("ccrrName")).isEqualTo(ACTIVE_SITE_ADMIN_RESOURCE_ORG.getName());
        assertThat(familySubmission.getInputData().get("ccrrPhoneNumber")).isEqualTo(ACTIVE_SITE_ADMIN_RESOURCE_ORG.getPhone());
    }

    @Test
    void shouldNotChangeFamilyOrgIdWhenProviderIsNotSiteAdministered() {
        providerSubmission.getInputData().put("providerResponseProviderNumber", CURRENT_APPROVED_PROVIDER.getProviderId().toString());
        submissionRepositoryService.save(providerSubmission);
        setResourceOrganization.run(providerSubmission);
        familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();
        assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo("testValue");
        assertThat(familySubmission.getInputData().get("ccrrName")).isEqualTo("CCRR Name");
        assertThat(familySubmission.getInputData().get("ccrrPhoneNumber")).isEqualTo("(123) 123-1234");
    }
}