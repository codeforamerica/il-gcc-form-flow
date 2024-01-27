package org.ilgcc.app.journeys;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.Page;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@Tag("staticPagesJourney")
@TestPropertySource(properties = {
    "form-flow.disabled-flows[0].flow=ubi",
    "form-flow.disabled-flows[0].staticRedirectPage=/",
    "form-flow.disabled-flows[1].flow=docUpload",
    "form-flow.disabled-flows[1].staticRedirectPage=/disabledFeature",
})
public class StaticHomepageDisabledFlowsJourneyTest extends AbstractBasePageTest {

  protected void initTestPage() {
    testPage = new Page(driver);
  }

  @Autowired
  Environment environment;

  @Test
  void staticPagesJourney() {
    assertThat(testPage.getTitle()).isEqualTo("Apply for UBI payments easily online.");
    List<WebElement> buttons = testPage.findElementsByClass("button");
    // When UBI flow is disabled, then no "apply now" button expected
    assertFalse(buttons.stream().anyMatch(button -> button.getText().equals("Apply now")));
    // When docUpload flow is disabled, then no "upload documents" button is expected
    assertFalse(buttons.stream().anyMatch(button -> button.getText().equals("Upload documents")));

    // Attempt to navigate to any ubi screen and expect to stay on the home screen
    driver.navigate().to(baseUrl + "/flow/ubi/howThisWorks");
    assertThat(testPage.getTitle()).isEqualTo("Apply for UBI payments easily online.");

    // Attempt to navigate to any docUpload screen and expect a redirect to static disabled flow screen
    driver.navigate().to(baseUrl + "/flow/docUpload/uploadDocuments");
    assertThat(testPage.getTitle()).isEqualTo("Sorry, that feature is currently unavailable");
  }
}
