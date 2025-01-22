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
class SetCaseLoadCodeAndCCRRNameTest {

    @Autowired
    ApplicationRouterService applicationRouterService;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    private SetCaseLoadCodeAndCCRRName action = new SetCaseLoadCodeAndCCRRName();

    @BeforeEach
    void setUp() {
        action.applicationRouterService = applicationRouterService;
        action.submissionRepositoryService = submissionRepositoryService;
    }

    @Test
    public void doesNotSetCaseLoadIfNoAddressComponents() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("caseloadCode")).isNull();
    }

    @Test
    public void setsCaseLoadFromValidatedParentMailingAddress() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withValidatedMailingAddress("123 Main St.", "Apt 2", "Chicago", "IL",
                        ZipcodeOption.zip_60304.getValue() + "-1234")
                .withMailingAddress("123 Main St.", "Apt 2", "Chicago", "IL", ZipcodeOption.zip_62479.getValue())
                .with("applicationCounty", CountyOption.LEE.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("caseloadCode").toString()).isEqualTo("GG");
    }

    @Test
    public void setsCaseLoadFromParentMailingAddressIfNotValidated() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withMailingAddress("123 Main St.", "Apt 2", "Chicago", "IL", ZipcodeOption.zip_62479.getValue())
                .with("applicationCounty", CountyOption.LEE.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("caseloadCode").toString()).isEqualTo("QQ");
    }

    @Test
    public void setsCaseLoadFromCountyWhenParentMailingAddressIsEmpty() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("applicationCounty", CountyOption.LEE.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("caseloadCode").toString()).isEqualTo("BB");
    }

    @Test
    public void setsCaseLoadFromApplicationZipCodeWhenNoOtherOptionExists() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("applicationZipCode", ZipcodeOption.zip_60304.getValue())
                .build();

        action.run(submission);

        assertThat(submission.getInputData().get("caseloadCode").toString()).isEqualTo("GG");
    }

}