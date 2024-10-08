package org.ilgcc.app.journeys;

import org.ilgcc.app.utils.AbstractMockMvcTest;
import org.ilgcc.app.utils.FormScreen;
import org.jetbrains.annotations.NotNull;
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
          .split("=")[1];
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static Stream<Arguments> invalidDates() {
    return Stream.of(
        Arguments.of("1", "1", "1889", "Make sure the date you entered is after 01/01/1901."),
        Arguments.of("*", "1", "1889", "Make sure the date you entered is in this format: mm/dd/yyyy"),
        Arguments.of("*1", "1", "1989", "Make sure the date you entered is in this format: mm/dd/yyyy")
    );
  }

  @ParameterizedTest
  @MethodSource("org.ilgcc.app.journeys.CcapStartDateTest#invalidDates")
  void testInvalidDates(String month, String day, String year, String expectedErrorMessage) throws Exception {
    postToIsInChildcarePage("true");

    postToChildCareStartDate(month, day, year);

    assertThat(getCcapStartDateError()).isEqualTo(expectedErrorMessage);
  }

  @NotNull
  private String getCcapStartDateError() throws Exception {
    return getCcapStartDatePage().getInputError("ccapStartDate").text();
  }

  @Test
  void testEmptyDateIsValid() throws Exception {
    postToIsInChildcarePage("true");

    postToChildCareStartDate("", "", "");

    FormScreen get = getCcapStartDatePage();
    assertThat(get.getInputError("ccapStartDate")).isNull();
  }

  private void postToChildCareStartDate(String month, String day, String year) throws Exception {
    Map<String, List<String>> params = Map.of(
        "current_uuid", List.of(uuid),
        "ccapStartMonth", List.of(month),
        "ccapStartDay", List.of(day),
        "ccapStartYear", List.of(year)
    );
    postToUrl(formatUrl("children-ccap-start-date"), params);
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
