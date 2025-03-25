package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
@TestPropertySource(properties = {"il-gcc.enable-provider-messaging=true"})
public class GccProviderMessagingFlowJourneyTest extends AbstractBasePageTest {

    @Test
    void ProviderMessagingSteps() {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentBasicInfo()
                .with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", "LEE")
                .build());

        testPage.clickYes();

        testPage.navigateToFlowScreen("gcc/submit-intro");

        // submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-intro.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("submit-intro.header.contact-provider"));
        testPage.clickContinue();
    }
}