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
    @Test
    void activeApplicationShowsSuccess() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .with("parentPreferredName", "FirstName")
                .withChild("First", "Child", "Yes")
                .withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now())
                .withShortCode(VALID_CONF_CODE)
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/providerresponse/submit?conf_code=%s&utm_source=test".formatted(localServerPort, VALID_CONF_CODE));
    }

    @Test
    void oldApplicationShowsExpiration() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
                .withParentDetails()
                .with("parentPreferredName", "FirstName")
                .withChild("First", "Child", "Yes")
                .withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.of(2010,10,10, 0,0,0,0,null))
                .withShortCode(VALID_CONF_CODE)
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/providerresponse/submit?conf_code=%s&utm_source=test".formatted(localServerPort, VALID_CONF_CODE));
    }

}