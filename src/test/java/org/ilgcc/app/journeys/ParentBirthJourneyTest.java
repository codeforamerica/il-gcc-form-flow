package org.ilgcc.app.journeys;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParentBirthJourneyTest extends AbstractBasePageTest {

  @Test
  void ParentJourneyTest() {
    // Home page
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("index.title"));
    testPage.clickButton(getEnMessage("index.apply-now"));
    // parent-info-basic-1
    testPage.navigateToFlowScreen("gcc/parent-info-basic-1");
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-basic-1.title"));
    testPage.enter("parentFirstName", "parent first");
    testPage.enter("parentLastName", "parent last");
    testPage.clickContinue();
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-basic-1.title"));
    assertThat(testPage.hasErrorText(getEnMessage("errors.provide-birthday"))).isTrue();
    testPage.enter("parentBirthMonth", "*");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1889");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-birthdate-format"))).isTrue();
    testPage.enter("parentBirthMonth", "*1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1989");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-birthdate-format"))).isTrue();
    testPage.enter("parentBirthMonth", "1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1700");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-date-range"))).isTrue();
    testPage.enter("parentBirthMonth", "1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "3100");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-date-range"))).isTrue();
    testPage.enter("parentBirthMonth", "1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1985");
    testPage.clickContinue();
    assertThat(testPage.getTitle()).isNotEqualTo(getEnMessage("parent-info-basic-1.title"));
  }
}
