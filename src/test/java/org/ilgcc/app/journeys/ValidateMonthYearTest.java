package org.ilgcc.app.journeys;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidateMonthYearTest extends AbstractBasePageTest {

  @Test
  void fullGccFlow() {
    // Home page
    assertThat(testPage.getTitle()).isEqualTo("Get help paying for child care.");
    testPage.clickButton("Apply now");

    //activities-ed-program-name
    driver.navigate().to(baseUrl + "/flow/gcc/activities-ed-program-name");
    testPage.enter("schoolName", "Test Data");
    testPage.clickContinue();
    // activities-ed-program-dates
    driver.navigate().to(baseUrl + "/flow/gcc/activities-ed-program-dates");
    assertThat(testPage.getTitle()).isEqualTo("Time of Program");
    testPage.enter("activitiesProgramStartMonth", "05");
    testPage.clickContinue();
    assertThat(testPage.getTitle()).isEqualTo("Time of Program");
    assertThat(testPage.hasErrorText("Make sure you entered the correct year.")).isTrue();
    testPage.enter("activitiesProgramStartYear", "1991");
    testPage.enter("activitiesProgramStartMonth", "ss");
    testPage.clickContinue();
    assertThat(testPage.getTitle()).isEqualTo("Time of Program");
    assertThat(testPage.hasErrorText("Make sure you entered the correct month.")).isTrue();

    testPage.enter("activitiesProgramStartMonth", "05");
    testPage.clickContinue();
    assertThat(testPage.getTitle()).isNotEqualTo("Time of Program");
  }
}
