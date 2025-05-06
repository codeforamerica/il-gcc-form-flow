package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"il-gcc.enable-multiple-providers=true"})
public class GccMultiProviderFlowJourneyTest extends AbstractBasePageTest {

    @Test
    void MultiProviderFlowJourneyTest() {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentBasicInfo()
                .with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", "LEE")
                .with("hasChosenProvider", "true")
                .withShortCode("familyShortCode")
                .build());

        testPage.navigateToFlowScreen("gcc/providers-intro");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-intro.title-header"));
    }
}
