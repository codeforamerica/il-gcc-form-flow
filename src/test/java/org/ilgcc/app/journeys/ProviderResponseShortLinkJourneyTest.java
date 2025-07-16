package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.time.OffsetDateTime;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ProviderResponseShortLinkJourneyTest extends AbstractBasePageTest {

    private static final String PROVIDER_NAME = "Brown Bear Daycare";

    @AfterEach
    protected void clearSubmissions() {
        super.clearSubmissions();
    }

    @Test
    void activeApplicationShowsSuccess() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission s = saveSubmission(getSessionSubmissionTestBuilder()
                .with("familyIntendedProviderName", PROVIDER_NAME)
                .withParentDetails()
                .with("parentPreferredName", "FirstName")
                .withChild("First", "Child", "true")
                .withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now())
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s/%s".formatted(localServerPort,
                s.getShortCode()));

        // submit-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("provider-response-submit-start.active.header", new Object[]{"Brown Bear Daycare"}));

        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));
    }

    @Test
    void oldApplicationShowsExpiration() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission s = saveSubmission(getSessionSubmissionTestBuilder()
                .with("familyIntendedProviderName", PROVIDER_NAME)
                .withParentDetails()
                .with("parentPreferredName", "FirstName")
                .withChild("First", "Child", "true")
                .withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s/%s".formatted(localServerPort,
                s.getShortCode()));

        // submit-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("provider-response-submit-start.expired.header", new Object[]{"Brown Bear Daycare"}));

        testPage.clickButton(getEnMessage("general.button.return.home"));
    }

//    TODO: Add already submitted logic once provider response can be submitted
}