package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class ParentBirthJourneyTest extends AbstractBasePageTest {

  @Test
  void fullGccFlow() {
    // Home page
    assertThat(testPage.getTitle()).isEqualTo("Get help paying for child care.");
    testPage.clickButton("Apply now");
    // parent-info-basic-1
    driver.navigate().to(baseUrl + "/flow/gcc/parent-info-basic-1");
    assertThat(testPage.getTitle()).isEqualTo("Tell us about yourself");
    testPage.enter("parentFirstName", "parent first");
    testPage.enter("parentLastName", "parent last");
    testPage.clickContinue();
    assertThat(testPage.getTitle()).isEqualTo("Tell us about yourself");
    assertThat(testPage.hasErrorText("Make sure to provide a birthday.")).isTrue();
    testPage.enter("parentBirthMonth", "*");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1889");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText("Make sure to provide a birthday.")).isTrue();
    testPage.enter("parentBirthMonth", "*1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1989");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText("Make sure to provide a birthday.")).isTrue();
    testPage.enter("parentBirthMonth", "1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1700");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText("Please enter a birth date between 01/01/1901 and today.")).isTrue();
    testPage.enter("parentBirthMonth", "1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "3100");
    testPage.clickContinue();
    assertThat(testPage.hasErrorText("Please enter a birth date between 01/01/1901 and today.")).isTrue();
    testPage.enter("parentBirthMonth", "1");
    testPage.enter("parentBirthDay", "1");
    testPage.enter("parentBirthYear", "1985");
    testPage.clickContinue();
    assertThat(testPage.getTitle()).isNotEqualTo("Tell us about yourself");
  }
}
