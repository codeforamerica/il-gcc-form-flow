package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.submission.router.ApplicationRouterService;
import org.ilgcc.app.utils.CountyOption;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.ZipcodeOption;
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
    private SetOrganizationIdAndCCRRName action = new SetOrganizationIdAndCCRRName();

    @BeforeEach
    void setUp() {
        action.applicationRouterService = applicationRouterService;
        action.submissionRepositoryService = submissionRepositoryService;
    }

    @Test
    public void doesNotSetOrganizationIdIfNoAddressComponents() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId")).isNull();
    }

    @Test
    public void setsOrganizationIdFromParentHomeAddressIfNotValidated() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withHomeAddress("123 Main St.", "Apt 2", "Chicago", "IL", ZipcodeOption.zip_62479.getValue())
                .with("applicationCounty", CountyOption.LEE.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo("59522729391675");
    }

    @Test
    public void setsOrganizationIdFromCountyWhenParentHomeAddressIsEmpty() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("applicationCounty", CountyOption.LEE.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo("56522729391679");
    }

    @Test
    public void setsOrganizationIdFromApplicationZipCodeWhenNoOtherOptionExists() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("applicationZipCode", ZipcodeOption.zip_60304.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("organizationId").toString()).isEqualTo("47522729391670");
    }

}