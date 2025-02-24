package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.data.FakeResourceOrganization.ACTIVE_FOUR_C_COUNTY;
import static org.ilgcc.app.data.FakeResourceOrganization.ACTIVE_OUT_OF_SCOPE_COUNTY;
import static org.ilgcc.app.data.FakeResourceOrganization.FOUR_C_TEST_DATA;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.importer.CCMSDataServiceImpl;
import org.ilgcc.app.submission.router.ApplicationRouterService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
class SetOrganizationIdAndCCRRNameTest {

    @Autowired
    ApplicationRouterService applicationRouterService;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    CCMSDataServiceImpl ccmsDataServiceImpl;

    private SetOrganizationIdAndCCRRName action = new SetOrganizationIdAndCCRRName();

    @BeforeEach
    void setUp() {
        action.applicationRouterService = applicationRouterService;
        action.submissionRepositoryService = submissionRepositoryService;
        action.ccmsDataServiceImpl = ccmsDataServiceImpl;
    }

    @Test
    public void setsResourceOrgBasedOnApplicationCountyIfNoHomeAddress() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("applicationCounty", ACTIVE_FOUR_C_COUNTY.getCounty())
                .build();

        action.run(submission);
        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo(
                FOUR_C_TEST_DATA.getResourceOrgId().toString());
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo(FOUR_C_TEST_DATA.getName());
        assertThat(submission.getInputData().get("applicantAddressCounty")).isNull();
    }

    @Test
    public void setsResourceOrgOnApplicationZipCodeIfNoHomeAddress() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("applicationZipCode", ACTIVE_FOUR_C_COUNTY.getZipCode().toString())
                .build();

        action.run(submission);
        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo(
                FOUR_C_TEST_DATA.getResourceOrgId().toString());
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo(FOUR_C_TEST_DATA.getName());
        assertThat(submission.getInputData().get("applicantAddressCounty")).isNull();
    }

    @Test
    public void setsResourceOrgBasedOnApplicationCountyIfHomeAddressZipCodeOutsideScopedSDAs() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withHomeAddress("123 Main St.", "Apt 2", "Chicago", "IL", ACTIVE_OUT_OF_SCOPE_COUNTY.getZipCode().toString())
                .with("applicationCounty", ACTIVE_FOUR_C_COUNTY.getCounty())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo(
                FOUR_C_TEST_DATA.getResourceOrgId().toString());
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo(FOUR_C_TEST_DATA.getName());
        assertThat(submission.getInputData().get("applicantAddressCounty").toString()).isEqualTo(
                ACTIVE_OUT_OF_SCOPE_COUNTY.getCounty());
    }

    @Test
    public void setsResourceOrgBasedOnApplicationZipIfHomeAddressZipCodeOutsideScopedSDAs() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withHomeAddress("123 Main St.", "Apt 2", "Chicago", "IL", ACTIVE_OUT_OF_SCOPE_COUNTY.getZipCode().toString())
                .with("applicationZipCode", ACTIVE_FOUR_C_COUNTY.getZipCode().toString())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo(
                FOUR_C_TEST_DATA.getResourceOrgId().toString());
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo(FOUR_C_TEST_DATA.getName());
        assertThat(submission.getInputData().get("applicantAddressCounty").toString()).isEqualTo(
                ACTIVE_OUT_OF_SCOPE_COUNTY.getCounty());
    }

    @Test
    public void setsResourceOrgFromUnvalidatedParentHomeAddressIfInSDAScope() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withHomeAddress("123 Main St.", "Apt 2", "Chicago", "IL", ACTIVE_FOUR_C_COUNTY.getZipCode().toString())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo(
                FOUR_C_TEST_DATA.getResourceOrgId().toString());
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo(FOUR_C_TEST_DATA.getName());
        assertThat(submission.getInputData().get("applicantAddressCounty").toString()).isEqualTo(
                ACTIVE_FOUR_C_COUNTY.getCounty());
    }

}