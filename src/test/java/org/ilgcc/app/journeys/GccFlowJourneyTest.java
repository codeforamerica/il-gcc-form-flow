package org.ilgcc.app.journeys;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    // parent-info-intro
    assertThat(testPage.getTitle()).isEqualTo("Parent and Guardian Information");
    testPage.clickContinue();
    // parent-info-basic-1
    assertThat(testPage.getTitle()).isEqualTo("Tell us about yourself");
    testPage.enter("parentFirstName", "parent first");
    testPage.enter("parentLastName", "parent last");
    testPage.enter("parentBirthMonth", "12");
    testPage.enter("parentBirthDay", "25");
    testPage.enter("parentBirthYear", "1985");
    testPage.clickContinue();
    // parent-info-basic-2
    assertThat(testPage.getTitle()).isEqualTo("Parent info basic");
    testPage.clickContinue();
    // parent-info-service
    assertThat(testPage.getTitle()).isEqualTo("Parent info service");
    testPage.clickContinue();
    // parent-home-address
    assertThat(testPage.getTitle()).isEqualTo("What is your home address?");
    testPage.enter("parentHomeStreetAddress1", "972 Mission St");
    testPage.enter("parentHomeStreetAddress2", "5th floor");
    testPage.enter("parentHomeCity", "San Francisco");
    testPage.selectFromDropdown("parentHomeState", "CA - California");
    testPage.enter("parentHomeZipCode", "94103");
    testPage.clickContinue();
    // parent-mailing-address
    assertThat(testPage.getTitle()).isEqualTo("Parent Mailing Address");
    testPage.goBack();
    //parent-home-address
    assertThat(testPage.getTitle()).isEqualTo("What is your home address?");
    testPage.clickElementById("parentHomeExperiencingHomelessness-yes");
    testPage.clickContinue();
    //parent-home-address
    assertThat(testPage.getTitle()).isEqualTo("Parent");
    testPage.clickButton("I have a place to get mail");
    // parent-mailing-address
    assertThat(testPage.getTitle()).isEqualTo("Parent Mailing Address");
    testPage.enter("parentMailingStreetAddress1", "972 Mission St");
    testPage.enter("parentMailingStreetAddress2", "5th floor");
    testPage.enter("parentMailingCity", "San Francisco");
    testPage.selectFromDropdown("parentMailingState", "CA - California");
    testPage.enter("parentMailingZipCode", "94103");
    testPage.clickContinue();
    // parent-confirm-address
    assertThat(testPage.getHeader()).isEqualTo("Confirm your address");
    testPage.clickButton("Use this address");
    // parent-contact
    assertThat(testPage.getTitle()).isEqualTo("Parent Contact");
    testPage.clickElementById("parentContactPreferCommunicate-mail-label");
    assertThat(testPage.getElementText("parentContactPreferCommunicate-mail-label")).isEqualTo("It's okay to send me mail about my case.");
    testPage.clickContinue();
    // parent-partner-contact
    assertThat(testPage.getTitle()).isEqualTo("How can we contact them?");
    testPage.clickContinue();

    //children-info-intro
    assertThat(testPage.getTitle()).isEqualTo("Your Children");
    testPage.clickContinue();
    // children-add
    assertThat(testPage.getTitle()).isEqualTo("Children add");
    testPage.clickButton("Add child");
    //children-info-basic
    assertThat(testPage.getTitle()).isEqualTo("Children Info");
    testPage.enter("childFirstName", "child");
    testPage.enter("childLastName", "mcchild");
    testPage.enter("childDateOfBirthMonth", "12");
    testPage.enter("childDateOfBirthDay", "25");
    testPage.enter("childDateOfBirthYear", "2018");
    testPage.selectRadio("needFinancialAssistanceForChild", "Yes");
    testPage.clickButton("Continue");
    //children-ccap-info
    assertThat(testPage.getTitle()).isEqualTo("CCAP Info");
    testPage.clickContinue();
    //children-ccap-in-care
    assertThat(testPage.getTitle()).isEqualTo("CCAP in care");
    testPage.clickButton("No");
    //children-ccap-start-date (Test No logic)
    assertThat(testPage.getTitle()).isEqualTo("CCAP Start Date");
    assertThat((testPage.getHeader())).isEqualTo("When will child start care at your chosen provider?");
    testPage.goBack();
    //children-ccap-start-date (Test Yes Logic)
    testPage.clickButton("Yes");
    assertThat(testPage.getTitle()).isEqualTo("CCAP Start Date");
    assertThat((testPage.getHeader())).isEqualTo("When did child start care at your chosen provider?");
    testPage.enter("ccapStartMonth", "11");
    testPage.enter("ccapStartDay", "1");
    testPage.enter("ccapStartYear", "2010");
    testPage.clickContinue();
    //children-ccap-weekly-schedule
    assertThat(testPage.getTitle()).isEqualTo("CCAP Childcare Weekly Schedule");
    testPage.clickContinue();
    //children-ccap-child-other-ed
    assertThat(testPage.getTitle()).isEqualTo("CCAP Child Other");
    testPage.clickButton("Yes");
    //children-add (with children listed)
    assertThat(testPage.getTitle()).isEqualTo("Children add");
    List<String> li = testPage.getTextBySelector(".child-name");
    assertThat(li).containsExactly("child mcchild");
    testPage.clickButton("That is all my children");

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
    assertThat(testPage.getTitle()).isEqualTo("Program information");
    testPage.clickContinue();

    //activities-ed-program-method
    assertThat(testPage.getTitle()).isEqualTo("How is the program taught?");
    testPage.clickElementById("programTaught-Online-label");
    testPage.clickElementById("programSchedule-No-label");
    testPage.clickContinue();

    //activities-next-class-schedule
    assertThat(testPage.getTitle()).isEqualTo("Next, we'll ask about your class schedule.");
    testPage.clickContinue();

    //activities-class-weekly-schedule
    assertThat(testPage.getTitle()).isEqualTo("Weekly Class Schedule");
    testPage.clickElementById("weeklySchedule-Monday");
    testPage.clickContinue();

    //activities-class-hourly-schedule
    assertThat(testPage.getTitle()).isEqualTo("Hourly Class Schedule");
    assertThat(testPage.getElementText("activitiesClassHoursSameEveryDay-Yes-label")).isEqualTo("My class hours are the same every day.");
    testPage.clickContinue();

    //activities-ed-program-dates
    assertThat(testPage.getTitle()).isEqualTo("Time of Program");
    //testPage.clickContinue();
  }
}
