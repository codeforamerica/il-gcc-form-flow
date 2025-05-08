package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"il-gcc.enable-multiple-providers=true", "il-gcc.enable-provider-messaging=true"})
public class GccMultiProviderFlowJourneyTest extends AbstractBasePageTest {

    @Test
    void MultiProviderNavigationWhenMoreThanOneChildNeedsCareJourneyTest() {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentBasicInfo()
                .with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", "LEE")
                .with("hasChosenProvider", "true")
                .withChild("First", "Child", "true")
                .withChild("Second", "Child", "true")
                .withShortCode("familyShortCode")
                .build());

        testPage.navigateToFlowScreen("gcc/children-add");
        testPage.clickButton(getEnMessage("children-add.thats-all"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-intro.title-header"));
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-chosen.title"));
        testPage.clickYes();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-all-ccap-children.title"));
        testPage.clickYes();
    }
}
