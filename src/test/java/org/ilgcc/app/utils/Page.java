package org.ilgcc.app.utils;

import io.percy.selenium.Percy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class Page {

  protected final RemoteWebDriver driver;

  protected final Percy percy;


  public Page(RemoteWebDriver driver) {
    this.driver = driver;
    this.percy = new Percy(driver);
  }

  public String getTitle() {
    checkForBadMessageKeys();
    percy.snapshot(driver.getTitle());
    return driver.getTitle();
  }

  public String getHeader() {
    checkForBadMessageKeys();
    String header = driver.findElement(By.id("header")).getText();
    percy.snapshot(header);
    return header;
  }

  private void checkForBadMessageKeys() {
    assertThat(driver.getTitle()).doesNotContain("??");
    assertThat(driver.findElement(By.xpath("/html")).getText()).doesNotContain("??");
  }

  public void goBack() {
    driver.findElement(By.partialLinkText("Go Back")).click();

    waitForFooterToLoad();
  }

  public void clickLink(String linkText) {
    checkForBadMessageKeys();
    driver.findElement(By.linkText(linkText)).click();
  }

  public void clickButton(String buttonText) {
    checkForBadMessageKeys();
    WebElement buttonToClick = driver.findElements(By.className("button")).stream()
        .filter(button -> button.getText().contains(buttonText))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No button found containing text: " + buttonText));
    buttonToClick.click();

    waitForFooterToLoad();
  }

  public void clickButtonLink(String buttonLinkText) {
    checkForBadMessageKeys();
    WebElement buttonToClick = driver.findElements(By.className("button--link")).stream()
        .filter(button -> button.getText().contains(buttonLinkText))
        .findFirst()
        .orElseThrow(
            () -> new RuntimeException("No button link found containing text: " + buttonLinkText));
    buttonToClick.click();

    waitForFooterToLoad();
  }

  public void clickContinue() {
    clickButton("Continue");
    waitForFooterToLoad();
  }

  public void enter(String inputName, String value) {
    checkForBadMessageKeys();
    List<WebElement> formInputElements = driver.findElements(By.name(inputName));
    WebElement firstElement = formInputElements.get(0);
    FormInputHtmlTag formInputHtmlTag = FormInputHtmlTag.valueOf(firstElement.getTagName());
    switch (formInputHtmlTag) {
      case select -> selectFromDropdown(firstElement, value);
      case button -> choose(formInputElements, value);
      case textarea -> enterInput(firstElement, value);
      case input -> {
        switch (InputTypeHtmlAttribute.valueOf(firstElement.getAttribute("type"))) {
          case text, time -> {
            enterInput(firstElement, value);
          }
          case radio, checkbox -> selectEnumeratedInput(formInputElements, value);
          default -> enterInput(firstElement, value);
        }
      }
      default -> throw new IllegalArgumentException("Cannot find element");
    }

  }

  public void enter(String inputName, List<String> value) {
    checkForBadMessageKeys();
    List<WebElement> formInputElements = driver.findElements(By.name(inputName + "[]")).stream()
        .filter(element -> !element.getAttribute("type").equals("hidden")).toList();
    WebElement firstElement = formInputElements.get(0);
    FormInputHtmlTag formInputHtmlTag = FormInputHtmlTag.valueOf(firstElement.getTagName());
    if (formInputHtmlTag == FormInputHtmlTag.input) {
      if (InputTypeHtmlAttribute.valueOf(firstElement.getAttribute("type"))
          == InputTypeHtmlAttribute.checkbox) {
        selectEnumeratedInput(formInputElements, value);
      } else {
        throw new IllegalArgumentException("Can't select multiple options for non-checkbox inputs");
      }
    } else {
      throw new IllegalArgumentException("Cannot find element");
    }
  }

  private void enterInput(WebElement webElement, String input) {
    if (!webElement.getAttribute("type").equals(InputTypeHtmlAttribute.radio.toString())) {
      webElement.clear();
    }
    webElement.sendKeys(input);
  }

  public void enterInputById(String inputId, String value) {
    WebElement we = driver.findElement(By.id(inputId));
    enterInput(we, value);
  }

  private void selectEnumeratedInput(List<WebElement> webElements, String optionText) {
    WebElement inputToSelect = webElements.stream()
        .map(input -> input.findElement(By.xpath("./..")))
        .filter(label -> label.getText().contains(optionText))
        .findFirst()
        .orElseThrow(
            () -> new RuntimeException(String.format("Cannot find value \"%s\"", optionText)));
    inputToSelect.click();
  }

  private void selectEnumeratedInput(List<WebElement> webElements, List<String> options) {
    options.forEach(option -> selectEnumeratedInput(webElements, option));
  }

  private void choose(List<WebElement> yesNoButtons, String value) {
    WebElement buttonToClick = yesNoButtons.stream()
        .filter(button -> button.getText().contains(value))
        .findFirst()
        .orElseThrow();
    buttonToClick.click();

    waitForFooterToLoad();
  }

  private void waitForFooterToLoad() {
    await().until(
        () -> !driver.findElements(By.className("main-footer")).get(0).getAttribute("innerHTML")
            .isBlank());
  }

  public void selectFromDropdown(String inputName, String optionText) {
    selectFromDropdown(
        driver.findElement(By.cssSelector(String.format("select[name='%s']", inputName))),
        optionText);
  }

  private void selectFromDropdown(WebElement webElement, String optionText) {
    WebElement optionToSelect = webElement
        .findElements(By.tagName("option")).stream()
        .filter(option -> option.getText().equals(optionText))
        .findFirst()
        .orElseThrow();
    optionToSelect.click();
  }

  public void selectRadio(String inputName, String optionText) {
    List<WebElement> webElements = driver.findElements(By.name(inputName));
    WebElement optionToSelect = webElements.stream()
        .filter(option -> option.getAttribute("value").equals(optionText))
        .findFirst()
        .orElseThrow();
    optionToSelect.click();
  }

  public WebElement getSelectedOption(String elementId) {
    return driver.findElement(By.id(elementId))
        .findElements(By.tagName("option")).stream()
        .filter(WebElement::isSelected)
        .findFirst()
        .orElseThrow();
  }

  public String getInputValue(String inputName) {
    return driver.findElement(By.cssSelector(String.format("input[name='%s[]']", inputName)))
        .getAttribute("value");
  }

  public String getInputAttribute(String inputName, String attribute) {
    return driver.findElement(By.cssSelector(String.format("input[name='%s']", inputName)))
        .getAttribute(attribute);
  }

  public String getElementText(String inputId) {
    return driver.findElement(By.id(inputId)).getText();
  }

  public String getCssSelectorText(String cssSelector) {
    return driver.findElement(By.cssSelector(cssSelector)).getText();
  }

  public String getBirthDateValue(String inputName, DatePart datePart) {
    return driver.findElement(
            By.cssSelector(
                String.format("input[name='%s[]']:nth-of-type(%s)", inputName, datePart.getPosition())))
        .getAttribute("value");
  }

  public String getRadioValue(String inputName) {
    return driver.findElements(By.cssSelector(String.format("input[name='%s[]']", inputName)))
        .stream()
        .filter(WebElement::isSelected)
        .map(input -> input.findElement(By.xpath("./..")).getText())
        .findFirst()
        .orElse(null);
  }

  public List<String> getCheckboxValues(String inputName) {
    return driver.findElements(By.cssSelector(String.format("input[name='%s[]']", inputName)))
        .stream()
        .filter(WebElement::isSelected)
        .map(input -> input.findElement(By.xpath("./..")).getText().split("\n")[0])
        .collect(Collectors.toList());
  }

  public List<String> getTextBySelector(String cssSelector) {
    List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
    return elements.stream()
        .map(WebElement::getText)
        .collect(Collectors.toList());
  }

  public String getSelectValue(String inputName) {
    return driver.findElement(By.cssSelector(String.format("select[name='%s[]']", inputName)))
        .findElements(By.tagName("option")).stream()
        .filter(WebElement::isSelected)
        .findFirst()
        .map(WebElement::getText)
        .orElseThrow();
  }

  public boolean hasInputError(String inputName) {
    return !driver.findElements(
        By.cssSelector(String.format("input[name='%s[]'] ~ p.text--error", inputName))).isEmpty();
  }

  public boolean selectHasInputError(String inputName) {
    return !driver.findElements(By.id(String.format("%s-error-p", inputName))).isEmpty();
  }

  public boolean hasErrorText(String errorMessage) {
    return driver.findElements(By.cssSelector("p.text--error > span"))
        .stream().anyMatch(webElement -> webElement.getText().equals(errorMessage));
  }

  public String getFirstInputError() {
    return driver.findElements(By.cssSelector("p.text--error > span")).stream().findFirst()
        .map(WebElement::getText).orElse(null);
  }

  public boolean inputIsValid(String inputName) {
    return driver.findElement(By.cssSelector(String.format("input[name='%s[]']", inputName)))
        .getAttribute("aria-invalid").equals("false");
  }

  public String getInputAriaLabel(String inputName) {
    return driver.findElement(By.cssSelector(String.format("input[name='%s[]']", inputName)))
        .getAttribute("aria-label");
  }

  public String getSelectAriaLabel(String inputName) {
    return driver.findElement(By.cssSelector(String.format("select[name='%s[]']", inputName)))
        .getAttribute("aria-label");
  }

  public String getInputAriaDescribedBy(String inputName) {
    return driver.findElement(By.cssSelector(String.format("input[name='%s[]']", inputName)))
        .getAttribute("aria-describedby");
  }

  public String getSelectAriaDescribedBy(String inputName) {
    return driver.findElement(By.cssSelector(String.format("select[name='%s[]']", inputName)))
        .getAttribute("aria-describedby");
  }

  public String getInputAriaLabelledBy(String inputName) {
    return getInputAriaLabelledBy("input", inputName);
  }

  public String getInputAriaLabelledBy(String elementType, String elementName) {
    return driver.findElement(
            By.cssSelector(String.format("%s[name='%s[]']", elementType, elementName)))
        .getAttribute("aria-labelledby");
  }

  public String findElementTextById(String id) {
    return driver.findElement(By.id(id)).getText();
  }

  public WebElement findElementById(String id) {
    return driver.findElement(By.id(id));
  }

  public List<WebElement> findElementsByClass(String className) {
    return driver.findElements(By.className(className));
  }

  public boolean elementDoesNotExistById(String id) {
    try {
      driver.findElement(By.id(id));
      return false;//element found, it does exist so return false
    } catch (org.openqa.selenium.NoSuchElementException e) {
      return true;//element not found, it does not exist
    }
  }


  public void clickElementById(String id) {
    WebElement inputToSelect = driver.findElement(By.id(id));
    inputToSelect.click();
  }

//  public void chooseSentiment(Sentiment sentiment) {
//    driver.findElement(
//            By.cssSelector(String.format("label[for='%s']", sentiment.name().toLowerCase())))
//        .click();
//  }

  enum FormInputHtmlTag {
    input,
    textarea,
    select,
    button
  }

  enum InputTypeHtmlAttribute {
    text,
    number,
    radio,
    checkbox,
    tel,
    hidden,
    time
  }
}
