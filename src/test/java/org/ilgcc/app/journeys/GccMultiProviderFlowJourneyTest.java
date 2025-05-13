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
    @Test
    void MultiProviderNavigationWhenNoProviderJourneyTest() {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder()
            .withParentBasicInfo()
            .with("familyIntendedProviderName", "ACME Daycare")
            .with("applicationCounty", "LEE")
            .withChild("First", "Child", "true")
            .withChild("Second", "Child", "true")
            .withShortCode("familyShortCode")
            .build());

        testPage.navigateToFlowScreen("gcc/providers-intro");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-intro.title-header"));
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-chosen.title"));
        testPage.clickNo();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-no-provider-intro.title"));
        testPage.clickButton(getEnMessage("providers-no-provider-intro.continue"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("activities-parent-intro.title"));
    }

    @Test
    void SkipsSingleProviderOnboardingScreens() {
        testPage.navigateToFlowScreen("gcc/onboarding-2-part");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-2-part.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("onboarding-2-part.header"));
        testPage.clickContinue();

        //onboarding-language-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-language-pref.title"));
        testPage.selectFromDropdown("languageRead", "English");
        testPage.selectFromDropdown("languageSpeak", "Español");
        testPage.clickContinue();

        // onboarding-county
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-county.title"));
        testPage.selectFromDropdown("applicationCounty", "DEKALB");
        testPage.clickContinue();

        // Skips all provider screens and goes to parent-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-intro.title"));
    }

    @Test
    void SkipsChildrenScheduleScreens() {


    }
}
