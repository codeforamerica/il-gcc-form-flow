package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.submission.router.ApplicationRouterService;
import org.ilgcc.app.utils.CountyOption;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.ZipcodeOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
class SetOrganizationIdAndCCRRNameTest {

    @Mock
    ApplicationRouterService applicationRouterService;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    private SetOrganizationIdAndCCRRName action = new SetOrganizationIdAndCCRRName();



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        action.applicationRouterService = applicationRouterService;
        action.submissionRepositoryService = submissionRepositoryService;
    }

    @Test
    public void doesNotSetOrganizationIdOrNameIfNoAddressComponents() {
        ResourceOrganization org = new ResourceOrganization();
        org.setName("TestOrg");
        org.setResourceOrgId(new BigInteger("4324324"));
        when(applicationRouterService.getOrganizationIdByZipCode(any())).thenReturn(Optional.of(org));
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .build();

        action.run(submission);
        assertThat(submission.getInputData().get("organizationId")).isNull();
        assertThat(submission.getInputData().get("ccrrName")).isNull();
    }

    @Test
    public void setsOrganizationIdFromParentHomeAddressIfNotValidated() {
        ResourceOrganization org = new ResourceOrganization();
        org.setName("Project CHILD");
        org.setResourceOrgId(new BigInteger("59522729391675"));
        when(applicationRouterService.getOrganizationIdByZipCode(any())).thenReturn(Optional.of(org));

        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withHomeAddress("123 Main St.", "Apt 2", "Chicago", "IL", ZipcodeOption.zip_62479.getValue())
                .with("applicationCounty", CountyOption.LEE.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo("59522729391675");
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo("Project CHILD");
    }

    @Test
    public void setsOrganizationIdFromCountyWhenUnhoused() {
        ResourceOrganization org = new ResourceOrganization();
        org.setName("4C: Community Coordinated Child Care");
        org.setResourceOrgId(new BigInteger("56522729391679"));
        when(applicationRouterService.getOrganizationIdByZipCode(any())).thenReturn(Optional.of(org));

        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentHomeExperiencingHomelessness[]", List.of(true))
                .with("applicationCounty", CountyOption.LEE.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo("56522729391679");
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo("4C: Community Coordinated Child Care");
    }

    @Test
    public void setsOrganizationIdFromApplicationWhenUnhoused() {
        ResourceOrganization org = new ResourceOrganization();
        org.setName("Illinois Action for Children");
        org.setResourceOrgId(new BigInteger("47522729391670"));
        when(applicationRouterService.getOrganizationIdByZipCode(any())).thenReturn(Optional.of(org));

        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentHomeExperiencingHomelessness[]", List.of(true))
                .with("applicationZipCode", ZipcodeOption.zip_60304.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo("47522729391670");
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo("Illinois Action for Children");
    }

    @Test
    public void setsOrganizationIdFromCountyWhenInvalidZipCode() {
        ResourceOrganization org = new ResourceOrganization();
        org.setName("4C: Community Coordinated Child Care");
        org.setResourceOrgId(new BigInteger("56522729391679"));
        when(applicationRouterService.getOrganizationIdByZipCode(any())).thenReturn(Optional.of(org));


        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withHomeAddress("123 Main St.", "Apt 2", "San Francisco", "Ca", "94114")
                .with("applicationCounty", CountyOption.LEE.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo("56522729391679");
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo("4C: Community Coordinated Child Care");
    }

    @Test
    public void setsOrganizationIdFromApplicationWhenInvalidZipCode() {

        ResourceOrganization org = new ResourceOrganization();
        org.setName("Illinois Action for Children");
        org.setResourceOrgId(new BigInteger("47522729391670"));
        when(applicationRouterService.getOrganizationIdByZipCode(any())).thenReturn(Optional.of(org));

        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withHomeAddress("123 Main St.", "Apt 2", "San Chicago", "Il", "69999")
                .with("applicationZipCode", ZipcodeOption.zip_60304.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo("47522729391670");
        assertThat(submission.getInputData().get("ccrrName").toString()).isEqualTo("Illinois Action for Children");
    }

}