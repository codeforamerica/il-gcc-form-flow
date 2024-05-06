package org.ilgcc.app.journeys;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParentBirthJourneyTest extends AbstractBasePageTest {

  @Test
  void ParentJourneyTest() {
    // Home page
    assertThat(testPage.getTitle()).isEqualTo("Get help paying for child care.");
    testPage.clickButton("Apply now");
    // parent-info-basic-1
    testPage.navigateToFlowScreen("gcc/parent-info-basic-1");
    assertThat(testPage.getTitle()).isEqualTo("Tell us about yourself");
    testPage.enter("parentFirstName", "parent first");
    testPage.enter("parentLastName", "parent last");
    testPage.clickContinue();
    assertThat(testPage.getTitle()).isEqualTo("Tell us about yourself");
    assertThat(testPage.hasErrorText("Please provide a birthdate.")).isTrue();
    testPage.enter("parentBirthMonth", "*");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1889");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText("Make sure the birthdate you entered is in this format: mm/dd/yyyy")).isTrue();
    testPage.enter("parentBirthMonth", "*1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1989");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText("Make sure the birthdate you entered is in this format: mm/dd/yyyy")).isTrue();
    testPage.enter("parentBirthMonth", "1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1700");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText("Make sure the date you entered is between 01/01/1901 and today.")).isTrue();
    testPage.enter("parentBirthMonth", "1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "3100");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText("Make sure the date you entered is between 01/01/1901 and today.")).isTrue();
    testPage.enter("parentBirthMonth", "1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1985");
    testPage.clickContinue();
    assertThat(testPage.getTitle()).isNotEqualTo("Tell us about yourself");
  }
}
