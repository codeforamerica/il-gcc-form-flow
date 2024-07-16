package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
    "form-flow.design-system.name=honeycrisp",
})
public class DesignSystemHoneycrispJourneyTest extends AbstractBasePageTest {

  @Autowired
  Environment environment;

  @Test
  void staticPagesJourney() {
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("index.title"));

    WebElement customLink = driver.findElement(By.cssSelector("link[href*='custom.css']"));
    assertThat(customLink).isNotNull();
    assertThat(customLink.getAttribute("href").contains("custom.css")).isTrue();

    WebElement honeycrispLink = driver.findElement(By.cssSelector("link[href*='honeycrisp.min.css']"));
    assertThat(honeycrispLink).isNotNull();
    assertThat(honeycrispLink.getAttribute("href").contains("honeycrisp.min.css")).isTrue();

    WebElement honeycrispScript = driver.findElement(By.cssSelector("script[src*='honeycrisp.min.js']"));
    assertThat(honeycrispScript).isNotNull();
    assertThat(honeycrispScript.getAttribute("src").contains("honeycrisp.min.js")).isTrue();

    var cfaUswdsLinkName = "styles.css";
    assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> driver.findElement(By.cssSelector("link[href*='" + cfaUswdsLinkName + "']")));

    var cfaUswdsScriptName = "scripts.js";
    assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> driver.findElement(By.cssSelector("script[src*='" + cfaUswdsScriptName +"']")));
  }
}
