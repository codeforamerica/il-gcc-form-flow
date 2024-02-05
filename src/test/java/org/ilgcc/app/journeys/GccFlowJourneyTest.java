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

    assertThat(testPage.getTitle()).isEqualTo("Now tell us about your school or training program.");
    testPage.clickContinue();

    assertThat(testPage.getTitle()).isEqualTo("What is the school or training program name?");
    testPage.enter("schoolName", "World");
    testPage.clickContinue();

  }
}
