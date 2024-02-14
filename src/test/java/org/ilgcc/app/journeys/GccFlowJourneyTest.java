package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class GccFlowJourneyTest extends AbstractBasePageTest {
  
  @Test
  void fullGccFlow() {
    // Home page
    assertThat(testPage.getTitle()).isEqualTo("Get help paying for child care.");
    testPage.clickButton("Apply now");
    // onboarding-getting-started
    assertThat(testPage.getTitle()).isEqualTo("Getting started");
    testPage.clickContinue();
    //onboarding-choose-provider
    assertThat(testPage.getTitle()).isEqualTo("Choose Provider");
    testPage.clickElementById("dayCareChoice-OPEN_SESAME-label");
    testPage.clickContinue();
    //onboarding-confirm-provider
    assertThat(testPage.getTitle()).isEqualTo("Confirm provider");
    testPage.clickLink("Yes, I confirm");
    //onboarding-language-preference
    assertThat(testPage.getTitle()).isEqualTo("Language Preference");
    testPage.selectFromDropdown("languageRead", "English");
    testPage.selectFromDropdown("languageSpeak", "Español");
    testPage.clickContinue();

    //children-info-intro
    assertThat(testPage.getTitle()).isEqualTo("Your Children");
    testPage.clickContinue();
    //children-info-basic
    assertThat(testPage.getTitle()).isEqualTo("Children Info");
    testPage.enter("childFirstName", "child");
    testPage.enter("childLastName", "mcchild");
    testPage.enter("childDateOfBirthMonth", "12");
    testPage.enter("childDateOfBirthDay", "25");
    testPage.enter("childDateOfBirthYear", "2020");
    testPage.selectRadio("needFinancialAssistanceForChild", "Yes");
    testPage.clickContinue();
    //activities-add-ed-program
    assertThat(testPage.getTitle()).isEqualTo("Tell us about your school or training program.");
    testPage.clickContinue();

    //activities-ed-program-type
    assertThat(testPage.getElementText("educationType-highSchool-label")).isEqualTo("High School or GED");
    testPage.clickElementById("educationType-highSchool-label");
    assertThat(testPage.getTitle()).isEqualTo("What type of school or training are you enrolled in?");
    testPage.clickContinue();

    //activities-ed-program-name
    testPage.enter("schoolName", "World");
    assertThat(testPage.getTitle()).isEqualTo("What is the school or training program name?");
    testPage.clickContinue();

    //activities-ed-program-info
    //testPage.clickContinue();

    //activities-ed-program-method
    //assertThat(testPage.getTitle()).isEqualTo("Learning style");
    //testPage.clickContinue();

    //activities-next-class-schedule
    //assertThat(testPage.getTitle()).isEqualTo("What days do you take classes?");
    //assertThat(testPage.getElementText("weeklySchedule-monday-label")).isEqualTo("Monday");
    //testPage.clickContinue();
  }
}
