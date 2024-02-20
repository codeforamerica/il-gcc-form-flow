package org.ilgcc.app.journeys;

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
    testPage.clickButton("Continue");
    //children-ccap-info
    assertThat(testPage.getTitle()).isEqualTo("CCAP Info");
    testPage.clickContinue();
    //children-ccap-in-care
    assertThat(testPage.getTitle()).isEqualTo("CCAP in care");
    testPage.clickButton("Yes");
    //children-ccap-start-date
    assertThat(testPage.getTitle()).isEqualTo("CCAP Start Date");
    testPage.enter("ccapStartMonth", "11");
    testPage.enter("ccapStartDay", "1");
    testPage.enter("ccapStartYear", "1889");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText("Please check the date entered. " + "11/1/1889" + " is not a supported start date.")).isTrue();
    testPage.enter("ccapStartMonth", "*1");
    testPage.enter("ccapStartDay", "1");
    testPage.enter("ccapStartYear", "1889");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText("Please check the date entered. It is not a valid date.")).isTrue();
    testPage.enter("ccapStartMonth", "11");
    testPage.enter("ccapStartDay", "1");
    testPage.enter("ccapStartYear", "2010");
    testPage.clickContinue();
    //children-ccap-weekly-schedule
    assertThat(testPage.getTitle()).isEqualTo("CCAP Childcare Weekly Schedule");

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
