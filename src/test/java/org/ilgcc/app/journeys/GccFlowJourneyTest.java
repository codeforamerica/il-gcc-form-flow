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
    assertThat(testPage.getElementText("dayCareChoice-none-label")).isEqualTo("None of the above");
    testPage.clickElementById("dayCareChoice-none-label");
    testPage.clickContinue();
    // language preference
    assertThat(testPage.getTitle()).isEqualTo("Language Preference");
    testPage.selectFromDropdown("languageRead", "English");
    testPage.selectFromDropdown("languageSpeak", "Español");
    testPage.clickContinue();
    //activities-add-ed-program
    assertThat(testPage.getTitle()).isEqualTo("Now tell us about your school or training program.");
    testPage.clickContinue();

    //activities-ed-program-name
    testPage.enter("schoolName", "World");
    assertThat(testPage.getTitle()).isEqualTo("What is the school or training program name?");
    testPage.clickContinue();

    //activities-ed-program-type
    testPage.clickElementById("educationType-highSchool");
    assertThat(testPage.getTitle()).isEqualTo("What type of school or training are you enrolled in?");
    testPage.clickContinue();

  }
}
