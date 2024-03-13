package org.ilgcc.app.utils;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class AccessibilityPage extends Page {

  public List<Rule> resultsList = new ArrayList<>();

  public AccessibilityPage(RemoteWebDriver driver) {
    super(driver);
  }


  @Override
  public void clickLink(String linkText) {
    super.clickLink(linkText);
    testAccessibility();
  }

  @Override
  public void clickButton(String buttonText) {
    super.clickButton(buttonText);
    testAccessibility();
  }

  @Override
  public void clickButtonLink(String buttonLinkText) {
    super.clickButtonLink(buttonLinkText);
    testAccessibility();
  }

  public void testAccessibility() {
    AxeBuilder builder = new AxeBuilder();
    builder.setOptions("""
        {   "resultTypes": ["violations"],
            "runOnly": {
                "type": "tag",
                "values": ["wcag2a", "wcag2aa", "wcag21a", "wcag21aa", "section508", "best-practice"]
            }
        }
        """);
    Results results = builder.analyze(driver);
    List<Rule> violations = results.getViolations();
    violations.forEach(rule -> rule.setUrl(getTitle()));
    resultsList.addAll(violations);
    log.info("Testing a11y on page %s, found %s violations".formatted(driver.getTitle(), violations.size()));
  }
}
