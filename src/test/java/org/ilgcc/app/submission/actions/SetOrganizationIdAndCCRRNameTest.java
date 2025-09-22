package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.data.importer.FakeResourceOrganizationAndCountyData.ACTIVE_FOUR_C_COUNTY;
import static org.ilgcc.app.data.importer.FakeResourceOrganizationAndCountyData.COHORT2_COUNTY;
import static org.ilgcc.app.data.importer.FakeResourceOrganizationAndCountyData.FOUR_C_TEST_DATA;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.CCMSDataServiceImpl;
import org.ilgcc.app.submission.router.ApplicationRouterService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
class SetOrganizationIdAndCCRRNameTest {

    @Autowired
    ApplicationRouterService applicationRouterService;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    CCMSDataServiceImpl ccmsDataServiceImpl;

    private final SetOrganizationIdAndCCRRName setOrganizationIdAndCCRRNameAction = new SetOrganizationIdAndCCRRName();

    @BeforeEach
    void setUp() {
        setOrganizationIdAndCCRRNameAction.applicationRouterService = applicationRouterService;
        setOrganizationIdAndCCRRNameAction.submissionRepositoryService = submissionRepositoryService;
        setOrganizationIdAndCCRRNameAction.ccmsDataServiceImpl = ccmsDataServiceImpl;
    }

    @Test
    public void setsResourceOrgBasedOnApplicationCountyIfNoHomeAddress() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("applicationCounty", "DEKALB")
                .build();

        setOrganizationIdAndCCRRNameAction.run(submission);
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

        setOrganizationIdAndCCRRNameAction.run(submission);
        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo(
                FOUR_C_TEST_DATA.getResourceOrgId().toString());
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo(FOUR_C_TEST_DATA.getName());
        assertThat(submission.getInputData().get("applicantAddressCounty")).isNull();
    }

    @Test
    public void setsResourceOrgBasedOnApplicationCountyIfHomeAddressZipCodeOutsideScopedSDAs() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withHomeAddress("123 Main St.", "Apt 2", "Chicago", "IL", COHORT2_COUNTY.getZipCode().toString())
                .with("applicationCounty", ACTIVE_FOUR_C_COUNTY.getCounty())
                .build();

        setOrganizationIdAndCCRRNameAction.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo(
                FOUR_C_TEST_DATA.getResourceOrgId().toString());
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo(FOUR_C_TEST_DATA.getName());
        assertThat(submission.getInputData().get("applicantAddressCounty").toString()).isEqualTo(
                COHORT2_COUNTY.getCounty());
    }

    @Test
    public void setsResourceOrgBasedOnApplicationZipIfHomeAddressZipCodeOutsideScopedSDAs() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withHomeAddress("123 Main St.", "Apt 2", "Chicago", "IL", COHORT2_COUNTY.getZipCode().toString())
                .with("applicationZipCode", ACTIVE_FOUR_C_COUNTY.getZipCode().toString())
                .build();

        setOrganizationIdAndCCRRNameAction.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo(
                FOUR_C_TEST_DATA.getResourceOrgId().toString());
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo(FOUR_C_TEST_DATA.getName());
        assertThat(submission.getInputData().get("applicantAddressCounty").toString()).isEqualTo(
                COHORT2_COUNTY.getCounty());
    }

    @Test
    public void setsResourceOrgFromUnvalidatedParentHomeAddressIfInSDAScope() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withHomeAddress("123 Main St.", "Apt 2", "Chicago", "IL", ACTIVE_FOUR_C_COUNTY.getZipCode().toString())
                .build();

        setOrganizationIdAndCCRRNameAction.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo(
                FOUR_C_TEST_DATA.getResourceOrgId().toString());
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo(FOUR_C_TEST_DATA.getName());
        assertThat(submission.getInputData().get("applicantAddressCounty").toString()).isEqualTo(
                ACTIVE_FOUR_C_COUNTY.getCounty());
    }

}