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
        Arguments.of("*", "1", "1889", "Please enter the date your child will start care in this format: mm/dd/yyyy"),
        Arguments.of("*1", "1", "1989", "Please enter the date your child will start care in this format: mm/dd/yyyy")
    );
  }

  private static Stream<Arguments> datesOutSideOfCurrentRange() {
    return Stream.of(
        Arguments.of("1", "1", "1889", "Please enter a start date between 01/01/1901 and today."),
        Arguments.of("1", "1", "2030", "Please enter a start date between 01/01/1901 and today.")
    );
  }

  @ParameterizedTest
  @MethodSource("org.ilgcc.app.journeys.CcapStartDateTest#invalidDates")
  void testInvalidDates(String month, String day, String year, String expectedErrorMessage) throws Exception {
    postToUrlExpectingSuccess(formatUrl("children-ccap-in-care"), formatRedirectUrl("children-ccap-in-care"),
        Map.of("childInCare", List.of("true")));

    Map<String, List<String>> params = Map.of(
        "current_uuid", List.of(uuid),
        "ccapStartMonth", List.of(month),
        "ccapStartDay", List.of(day),
        "ccapStartYear", List.of(year)
    );
    postExpectingFailure("children-ccap-start-date", params, "children-ccap-start-date");

    FormScreen get = new FormScreen(getUrl(formatUrl("children-ccap-start-date")));
    assertThat(get.getInputError("ccapStartDate").text()).isEqualTo(expectedErrorMessage);
  }

  @ParameterizedTest
  @MethodSource("org.ilgcc.app.journeys.CcapStartDateTest#datesOutSideOfCurrentRange")
  void testDatesOutOfRangeForCurrentCare(String month, String day, String year, String expectedErrorMessage) throws Exception {
    postToUrlExpectingSuccess(formatUrl("children-ccap-in-care"), formatRedirectUrl("children-ccap-in-care"),
        Map.of("childInCare", List.of("true")));

    Map<String, List<String>> params = Map.of(
        "current_uuid", List.of(uuid),
        "ccapStartMonth", List.of(month),
        "ccapStartDay", List.of(day),
        "ccapStartYear", List.of(year)
    );
    postToUrlExpectingFailure(formatUrl("children-ccap-start-date"), params);

    FormScreen get = new FormScreen(getUrl(formatUrl("children-ccap-start-date")));
    assertThat(get.getInputError("ccapStartDate").text()).isEqualTo(expectedErrorMessage);
  }

  @Test
  void testDatesOutOfRangeForFutureCare() throws Exception {
    postToUrlExpectingSuccess(formatUrl("children-ccap-in-care"), "/flow/gcc/children-ccap-in-care/navigation?uuid=" + uuid,
        Map.of("childInCare", List.of("false")));

    Map<String, List<String>> params = Map.of(
        "current_uuid", List.of(uuid),
        "ccapStartMonth", List.of("1"),
        "ccapStartDay", List.of("1"),
        "ccapStartYear", List.of("2024")
    );
    postExpectingFailure("children-ccap-start-date", params, "children-ccap-start-date");

    FormScreen get = new FormScreen(getUrl(formatUrl("children-ccap-start-date")));
    assertThat(get.getInputError("ccapStartDate").text()).isEqualTo("Please choose a future start date.");
  }

  private String formatUrl(String pageName) {
    return "/flow/gcc/%s/%s".formatted(pageName, uuid);
  }

  private String formatRedirectUrl(String pageName) {
    return "/flow/gcc/%s/navigation?uuid=%s".formatted(pageName, uuid);
  }

}
