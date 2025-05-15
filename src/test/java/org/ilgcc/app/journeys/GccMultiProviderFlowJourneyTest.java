package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"il-gcc.enable-multiple-providers=true", "il-gcc.enable-provider-messaging=true"})
public class GccMultiProviderFlowJourneyTest extends AbstractBasePageTest {

  @Test
  void AllowsForThreeProvidersToBeAddedWhenClientAnswersYesToHavingChosenAProviderForEveryChildJourneyTest() {
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

    //providers-intro
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-intro.title-header"));
    testPage.clickContinue();

    //providers-chosen
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-chosen.title"));
    testPage.clickYes();

    //providers-all-ccap-children
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-all-ccap-children.title"));
    testPage.clickYes();

    //First Iteration
    //providers-add
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
    assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
    testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

    //providers-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
    assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-info.header"));
    testPage.clickContinue();

    assertThat(testPage.hasErrorText(getEnMessage("errors.provide-street"))).isTrue();
    assertThat(testPage.hasErrorText(getEnMessage("errors.provide-city"))).isTrue();

    testPage.enter("familyIntendedProviderName", "ACME Daycare");
    testPage.enter("familyIntendedProviderAddress", "101 Test St");
    testPage.enter("familyIntendedProviderCity", "Chicago");
    testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
    testPage.enter("familyIntendedProviderZipCode", "60302");
    testPage.clickContinue();

    //providers-contact-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
    testPage.enter("familyIntendedProviderEmail", "test.com");
    testPage.enter("familyIntendedProviderPhoneNumber", "555-55");
    testPage.clickContinue();

    assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-phone-number"))).isTrue();
    assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-email"))).isTrue();

    testPage.enter("familyIntendedProviderEmail", "test@test.com");
    testPage.enter("familyIntendedProviderPhoneNumber", "(555)555-5555");
    testPage.clickContinue();

    //providers-add
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
    assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
    assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
    testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

    //Second Iteration
    //provider-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
    testPage.enter("familyIntendedProviderName", "Nope Test");
    testPage.enter("familyIntendedProviderAddress", "151 Second St");
    testPage.enter("familyIntendedProviderCity", "Chicago");
    testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
    testPage.enter("familyIntendedProviderZipCode", "60402");
    testPage.clickContinue();

    //providers-contact-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
    testPage.enter("familyIntendedProviderEmail", "second@test.com");
    testPage.enter("familyIntendedProviderPhoneNumber", "(333)333-2222");
    testPage.clickContinue();

    //providers-add
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
    assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
    testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

    //Third Iteration
    //provider-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
    testPage.enter("familyIntendedProviderName", "Third Provider");
    testPage.enter("familyIntendedProviderAddress", "441 Third St");
    testPage.enter("familyIntendedProviderCity", "Chicago");
    testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
    testPage.enter("familyIntendedProviderZipCode", "60702");
    testPage.clickContinue();

    //providers-contact-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
    testPage.enter("familyIntendedProviderEmail", "third@test.com");
    testPage.enter("familyIntendedProviderPhoneNumber", "(243)555-5555");
    testPage.clickContinue();

    //providers-add
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
    assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header.max-providers-reached"));
    assertThat(testPage.findElementTextById("continue-link")).isNotEqualTo(getEnMessage("providers-add.button.that-is-all"));
    assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("general.button.continue"));
    testPage.clickContinue();
  }

  @Test
  void AllowsOnlyTwoProvidersToBeAddedWhenClientAnswersNoToHavingChosenAProviderForEveryChildJourneyTest() {
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
    //providers-intro
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-intro.title-header"));
    testPage.clickContinue();

    //providers-chosen
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-chosen.title"));
    testPage.clickYes();

    //providers-all-ccap-children
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-all-ccap-children.title"));
    testPage.clickNo();

    //First Iteration
    //providers-add
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
    assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
    testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

    //providers-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));

    testPage.enter("familyIntendedProviderName", "First Daycare");
    testPage.enter("familyIntendedProviderAddress", "222 Test St");
    testPage.enter("familyIntendedProviderCity", "Chicago");
    testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
    testPage.enter("familyIntendedProviderZipCode", "60302");
    testPage.clickContinue();

    //providers-contact-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));

    testPage.enter("familyIntendedProviderEmail", "test@first.com");
    testPage.enter("familyIntendedProviderPhoneNumber", "(533)555-5555");
    testPage.clickContinue();

    //providers-add
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
    assertThat(testPage.getHeader()).isEqualTo(getEnMessage("providers-add.header"));
    assertThat(testPage.findElementTextById("continue-link")).isEqualTo(getEnMessage("providers-add.button.that-is-all"));
    testPage.clickButton(getEnMessage("providers-add.button.add-a-provider"));

    //Second Iteration
    //provider-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
    testPage.enter("familyIntendedProviderName", "No Provider");
    testPage.enter("familyIntendedProviderAddress", "323 Second St");
    testPage.enter("familyIntendedProviderCity", "Chicago");
    testPage.selectFromDropdown("familyIntendedProviderState", "IL - Illinois");
    testPage.enter("familyIntendedProviderZipCode", "62202");
    testPage.clickContinue();

    //providers-contact-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
    testPage.enter("familyIntendedProviderEmail", "test@second.com");
    testPage.enter("familyIntendedProviderPhoneNumber", "(355)333-2222");
    testPage.clickContinue();

    //providers-add
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
    assertThat(testPage.findElementTextById("continue-link")).isNotEqualTo(getEnMessage("providers-add.button.that-is-all"));
    testPage.clickLink(getEnMessage("general.edit"));

    //providers-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-info.title"));
    assertThat(testPage.getInputValue("familyIntendedProviderName")).isEqualTo("First Daycare");
    testPage.clickContinue();

    //providers-contact-info
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-contact-info.title"));
    testPage.clickContinue();

    //providers-add
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("providers-add.title"));
    testPage.clickContinue();

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
        testPage.navigateToFlowScreen("gcc/parent-intro-family-info");

        // parent-intro-family-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-intro-family-info.title"));
        testPage.clickButton(getEnMessage("parent-intro-family-info.continue"));

        //children-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-intro.title"));
        testPage.clickContinue();

        // children-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-add.title"));
        testPage.clickButton(getEnMessage("children-add.add-button"));

        //children-info-basic
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-basic.title"));
        testPage.enter("childFirstName", "child");
        testPage.enter("childLastName", "mcchild");
        testPage.enter("childDateOfBirthMonth", "12");
        testPage.enter("childDateOfBirthDay", "25");
        testPage.enter("childDateOfBirthYear", "2018");
        testPage.selectFromDropdown("childRelationship", getEnMessage("general.relationship-option.foster-child"));
        testPage.clickContinue();

        //children-info-assistance
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-assistance.title"));
        testPage.clickYes();

        //children-ccap-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-info.title"));
        testPage.clickElementById("childGender-MALE");
        testPage.clickElementById("none__checkbox-childRaceEthnicity");
        testPage.clickContinue();

        // skips children-ccap-in-care, children-ccap-start-date, children-ccap-weekly-schedule, and children-childcare-hourly-schedule

        //children-ccap-child-other-ed
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-child-other-ed.title"));
    }
}
