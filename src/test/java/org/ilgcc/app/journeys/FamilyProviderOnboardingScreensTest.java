package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.CountyOption;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "REVEAL_ADDITIONAL_PROVIDER_SCREENS=true"
})
public class FamilyProviderOnboardingScreensTest extends AbstractBasePageTest {

    @Test
    void FamilyProviderOnboardingScreensTest() {
        testPage.navigateToFlowScreen("gcc/onboarding-county");

        // onboarding-county
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-county.title"));
        testPage.clickLink(getEnMessage("onboarding-county.link"));

        // onboarding-zipcode
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-zipcode.title"));
        testPage.enter("applicationZipCode", "40123");
        testPage.clickLink(getEnMessage("onboarding-zipcode.link"));

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-county.title"));
        testPage.selectFromDropdown("applicationCounty", CountyOption.LEE.getLabel());
        testPage.clickContinue();

        //onboarding-language-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-language-pref.title"));
        testPage.selectFromDropdown("languageRead", "English");
        testPage.selectFromDropdown("languageSpeak", "Español");
        testPage.clickContinue();

        // onboarding-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-getting-started.title"));
        testPage.clickContinue();

        // onboarding-chosen-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-chosen-provider.title"));
        testPage.clickNo();

        // offboarding-no-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("offboarding-no-provider.title"));
        testPage.goBack();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-chosen-provider.title"));
        testPage.clickYes();

        // onboarding-provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info.title"));
        testPage.enter("familyIntendedProviderName", "Provider Name");
        testPage.enter("familyIntendedProviderEmail", "mail@mail.com");
        testPage.enter("familyIntendedProviderPhoneNumber", "1234567890");
        testPage.clickContinue();

        // onboarding-provider-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info-review.title"));
        testPage.clickLink(getEnMessage("onboarding-provider-info-review.link"));

        // onboarding-provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info.title"));
        testPage.clickContinue();

        // onboarding-provider-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info-review.title"));
        testPage.clickContinue();

        // onboarding-provider-info-confirm
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info-confirm.title"));
        testPage.clickContinue();

        // parent-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-intro.title"));
    }
}