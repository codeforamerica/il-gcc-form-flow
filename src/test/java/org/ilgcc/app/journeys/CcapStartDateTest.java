package org.ilgcc.app.journeys;

import org.ilgcc.app.utils.AbstractMockMvcTest;
import org.ilgcc.app.utils.FormScreen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CcapStartDateTest extends AbstractMockMvcTest {

  private String uuid;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();

    try {
      // Initialize child flow
      uuid = postAndGetRedirectUrl("/flow/gcc/children-info-basic/new",
          Map.of(
              "childFirstName", List.of("Chilly"),
              "childLastName", List.of("Willy"),
              "childDateOfBirthMonth", List.of("10"),
              "childDateOfBirthDay", List.of("10"),
              "childDateOfBirthYear", List.of("2015")))
          .getResponse().getRedirectedUrl().split("=")[1];
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static Stream<Arguments> invalidDates() {
    return Stream.of(
        Arguments.of("1", "1", "2050", "Please enter a start date between 01/01/1901 and today."),
        Arguments.of("1", "1", "1889", "Please enter a start date between 01/01/1901 and today."),
        Arguments.of("*", "1", "1889", "Please enter the date your child started care in this format: mm/dd/yyyy"),
        Arguments.of("*1", "1", "1989", "Please enter the date your child started care in this format: mm/dd/yyyy")
    );
  }

  @ParameterizedTest
  @MethodSource("org.ilgcc.app.journeys.CcapStartDateTest#invalidDates")
  void testInvalidDates(String month, String day, String year, String expectedErrorMessage) throws Exception {
    postToIsInChildcarePage("true");

    Map<String, List<String>> params = buildParams(month, day, year);
    postToUrl(formatUrl("children-ccap-start-date"), params);

    FormScreen get = getCcapStartDatePage();
    assertThat(get.getInputError("ccapStartDate").text()).isEqualTo(expectedErrorMessage);
  }

  @Test
  void testDatesOutOfRangeForFutureCare() throws Exception {
    postToIsInChildcarePage("false");

    Map<String, List<String>> params = buildParams("1", "1", "2024");
    postToUrl(formatUrl("children-ccap-start-date"), params);

    FormScreen get = getCcapStartDatePage();
    assertThat(get.getInputError("ccapStartDate").text()).isEqualTo("Please choose a future start date.");
  }

  @Test
  void testEmptyDateIsValid() throws Exception {
    postToIsInChildcarePage("true");

    Map<String, List<String>> params = buildParams("", "", "");
    postToUrl(formatUrl("children-ccap-start-date"), params);

    FormScreen get = getCcapStartDatePage();
    assertThat(get.getInputError("ccapStartDate")).isNull();
  }

  private Map<String, List<String>> buildParams(String month, String day, String year) {
    return Map.of(
        "current_uuid", List.of(uuid),
        "ccapStartMonth", List.of(month),
        "ccapStartDay", List.of(day),
        "ccapStartYear", List.of(year)
    );
  }

  private void postToIsInChildcarePage(String value) throws Exception {
    String postUrl = formatUrl("children-ccap-in-care");
    Map<String, List<String>> params = Map.of("childInCare", List.of(value));
    postToUrl(postUrl, params);
  }

  private FormScreen getCcapStartDatePage() throws Exception {
    return new FormScreen(getFromUrl(formatUrl("children-ccap-start-date")));
  }

  private String formatUrl(String pageName) {
    return "/flow/gcc/%s/%s".formatted(pageName, uuid);
  }

}
