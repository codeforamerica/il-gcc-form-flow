package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
                .inputData(Map.of("organizationId", "testValue"))
                .shortCode("testShortCode")
                .build();
        submissionRepositoryService.save(familySubmission);
        HashMap<String, Object> providerSubmissionData = new HashMap<>();
        providerSubmissionData.put("providerResponseProviderNumber", "12345678909");
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
        assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo(10101);
    }

    @Test
    void shouldNotChangeFamilyOrgIdWhenProviderIsNotSiteAdministered() {
        providerSubmission.getInputData().put("providerResponseProviderNumber", "12345678901");
        submissionRepositoryService.save(providerSubmission);
        setResourceOrganization.run(providerSubmission);
        familySubmission = submissionRepositoryService.findById(familySubmission.getId()).orElseThrow();
        assertThat(familySubmission.getInputData().get("organizationId")).isEqualTo("testValue");
    }
}