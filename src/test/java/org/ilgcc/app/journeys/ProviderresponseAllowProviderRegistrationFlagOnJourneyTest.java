package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@TestPropertySource(properties = {"il-gcc.allow-provider-registration-flow=true"})
public class ProviderresponseAllowProviderRegistrationFlagOnJourneyTest extends AbstractBasePageTest {

    @Autowired
    SubmissionRepository repository;

    private static String CONF_CODE = "A2123B";

    @Test
    void ProviderresponseJourneyTest_validLink() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "No")
                .withChild("NoAssistance", "Child", "No").withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now()).withShortCode(CONF_CODE).build());

        testPage.clickContinue();

        driver.navigate()
                .to("http://localhost:%s/providerresponse/submit/%s?utm_medium=test".formatted(localServerPort, CONF_CODE));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // paid-by-ccap
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("paid-by-ccap.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("paid-by-ccap.header"));
        assertThat(testPage.elementDoesNotExistById("providerPaidCcap-true")).isFalse();
        assertThat(testPage.elementDoesNotExistById("providerPaidCcap-false")).isFalse();
        assertThat(testPage.elementDoesNotExistById("not-sure-providerPaidCcap-link")).isFalse();
    }


    @Test
    void RegisterAsCCAPProvider() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "No")
                .withChild("NoAssistance", "Child", "No").withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now()).withShortCode(CONF_CODE).build());

        testPage.clickContinue();

        driver.navigate()
                .to("http://localhost:%s/providerresponse/submit/%s?utm_medium=test".formatted(localServerPort, CONF_CODE));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // paid-by-ccap
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("paid-by-ccap.title"));
        testPage.clickNo();

        // registration-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-start.title"));
        testPage.clickButton(getEnMessage("registration-start.button"));

        // registration-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-getting-started.title"));
        testPage.clickContinue();

        // registration-provide-care-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-provide-care-intro.title"));
        testPage.clickContinue();

        // registration-licensing
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensing.title"));
        testPage.clickYes();

        // registration-licensing-info
//        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-licensing-info.title"));
        testPage.goBack();

        // registration-licensing
        testPage.clickNo();

        // registration-applicant
//        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-applicant.title"));
    }


}
