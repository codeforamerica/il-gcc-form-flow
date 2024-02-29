package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class ParentBirthJourneyTest extends AbstractBasePageTest {

  @Test
  void fullGccFlow() {
    // parent-info-basic-1
    driver.navigate().to(baseUrl + "/flow/gcc/parent-info-basic-1");
    assertThat(testPage.getTitle()).isEqualTo("Tell us about yourself");
    testPage.enter("parentFirstName", "parent first");
    testPage.enter("parentLastName", "parent last");
//    testPage.enter("parentBirthMonth", "12");
//    testPage.enter("parentBirthDay", "25");
//    testPage.enter("parentBirthYear", "1985");
    testPage.clickContinue();
    assertThat(testPage.getTitle()).isEqualTo("Tell us about yourself");
    assertThat(testPage.getTitle()).isEqualTo("Please check the date entered.");
  }
}
