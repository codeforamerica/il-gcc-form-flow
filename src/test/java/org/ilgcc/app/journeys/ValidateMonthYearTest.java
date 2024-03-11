package org.ilgcc.app.journeys;

import org.ilgcc.app.utils.AbstractBasePageTest;
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

public class ValidateMonthYearTest extends AbstractMockMvcTest {

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();

    try {

      postAndGetRedirectUrl("/flow/gcc/activities-ed-program-name",
              Map.of(
                      "schoolName", List.of("Chilly")));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static Stream<Arguments> invalidDates() {
    return Stream.of(
            Arguments.of("ss", "1", "2050", "Make sure you entered the correct month.", "activitiesProgramStartMonth"),
            Arguments.of("1", "1", "54333", "Make sure you entered the correct year.", "activitiesProgramStartYear")
    );
  }

  @ParameterizedTest
  @MethodSource("org.ilgcc.app.journeys.ValidateMonthYearTest#invalidDates")
  void testInvalidDates(String month, String day, String year, String expectedErrorMessage, String field) throws Exception {
    postToIsInActivitiesPartnerAddJobPage();

    postToActivitiesProgramDatesStartDate(month, day, year);

    assertThat(getActivitiesProgramDateError(field)).isEqualTo(expectedErrorMessage);
  }

  @NotNull
  private String getActivitiesProgramDateError(String field) throws Exception {
    return getActivitiesProgramStartDatePage().getInputError(field).text();
  }

  private void postToActivitiesProgramDatesStartDate(String month, String day, String year) throws Exception {
    Map<String, List<String>> params = Map.of(
            "activitiesProgramStartMonth", List.of(month),
            "activitiesProgramStartDay", List.of(day),
            "activitiesProgramStartYear", List.of(year)
    );
    postToUrl(formatUrl("activities-partner-add-job"), params);
  }

  private void postToIsInActivitiesPartnerAddJobPage() throws Exception {
    String postUrl = formatUrl("activities-partner-add-job");
    Map<String, List<String>> params = Map.of();
    postToUrl(postUrl, params);
  }

  private FormScreen getActivitiesProgramStartDatePage() throws Exception {
    return new FormScreen(getFromUrl(formatUrl("activities-ed-program-dates")));
  }

  private String formatUrl(String pageName) {
    return "/flow/gcc/%s".formatted(pageName);
  }

}
