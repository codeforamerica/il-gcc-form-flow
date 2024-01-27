package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.Page;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

@Tag("staticPagesJourney")
@TestPropertySource(properties = {
    "form-flow.design-system.name=cfa-uswds",
})
public class DesignSystemCfaUswdsJourneyTest extends AbstractBasePageTest {

  protected void initTestPage() {
    testPage = new Page(driver);
  }

  @Autowired
  Environment environment;

  @Test
  void staticPagesJourney() {
    assertThat(testPage.getTitle()).isEqualTo("Get help paying for child care.");

    var cfaUswdsLinkName = "styles.css";
    WebElement cfaUswdsLink = driver.findElement(By.cssSelector("link[href*='" + cfaUswdsLinkName + "']"));
    assertThat(cfaUswdsLink).isNotNull();
    assertThat(cfaUswdsLink.getAttribute("href").contains(cfaUswdsLinkName)).isTrue();

    var cfaUswdsScriptName = "scripts.js";
    WebElement cfaUswdsScript = driver.findElement(By.cssSelector("script[src*='" + cfaUswdsScriptName +"']"));
    assertThat(cfaUswdsScript).isNotNull();
    assertThat(cfaUswdsScript.getAttribute("src").contains(cfaUswdsScriptName)).isTrue();

    var customLinkName = "custom.css";
    assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> driver.findElement(By.cssSelector("link[href*='" + customLinkName + "']")));

    var honeycrispLinkName = "honeycrisp.min.css";
    assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> driver.findElement(By.cssSelector("link[href*='" + honeycrispLinkName + "']")));

    var honeycrispScriptName = "honeycrisp.min.js";
    assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> driver.findElement(By.cssSelector("script[src*='" + honeycrispScriptName +"']")));
  }
}
