package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.SubmissionRepository;
import java.time.OffsetDateTime;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProviderResponseShortLinkJourneyTest extends AbstractBasePageTest {

    @Autowired
    SubmissionRepository repository;

    private static String VALID_CONF_CODE="A2123B";
    private static String PROVIDER_NAME="Brown Bear Daycare";

    @Test
    void activeApplicationShowsSuccess() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder()
                .with("familyIntendedProviderName", PROVIDER_NAME)
                .withParentDetails()
                .with("parentPreferredName", "FirstName")
                .withChild("First", "Child", "Yes")
                .withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now())
                .withShortCode(VALID_CONF_CODE)
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s/%s".formatted(localServerPort,
                VALID_CONF_CODE));

        // submit-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessageWithParams("provider-response-submit-start.active.header", new Object[]{"Brown Bear Daycare"}));

        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));
    }

    @Test
    void oldApplicationShowsExpiration() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder()
                .with("familyIntendedProviderName", PROVIDER_NAME)
                .withParentDetails()
                .with("parentPreferredName", "FirstName")
                .withChild("First", "Child", "Yes")
                .withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(7))
                .withShortCode(VALID_CONF_CODE)
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s/%s".formatted(localServerPort,
                VALID_CONF_CODE));

        // submit-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessageWithParams("provider-response-submit-start.expired.header", new Object[]{"Brown Bear Daycare"}));

        testPage.clickButton(getEnMessage("provider-response-submit-start.expired.button"));
    }

//    TODO: Add already submitted logic once provider response can be submitted
}