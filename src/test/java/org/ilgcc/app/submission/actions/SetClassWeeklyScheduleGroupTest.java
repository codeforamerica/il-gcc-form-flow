package org.ilgcc.app.submission.actions;

import formflow.library.data.Submission;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class SetClassWeeklyScheduleGroupTest {

  private SetClassWeeklyScheduleGroup action;

  @BeforeEach
  void setup() {
    MessageSource messageSource = Mockito.mock(MessageSource.class);
    action = new SetClassWeeklyScheduleGroup(messageSource);

    Stream.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        .forEach(d -> when(messageSource.getMessage(eq("general.week." + d), any(), any())).thenReturn(d));
  }

  @ParameterizedTest
  @MethodSource("org.ilgcc.app.submission.actions.SetClassWeeklyScheduleGroupTest#consecutiveDaysArgs")
  void shouldShortenConsecutiveDays(List<String> weeklySchedule, String expected) {
    Submission submission = new SubmissionTestBuilder()
        .with("weeklySchedule[]", weeklySchedule)
        .build();
    action.run(submission);

    assertThat(submission.getInputData().get("displayWeeklySchedule")).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("org.ilgcc.app.submission.actions.SetClassWeeklyScheduleGroupTest#dispersedDaysArgs")
  void shouldListDispersedDays(List<String> weeklySchedule, String expected) {
    Submission submission = new SubmissionTestBuilder()
        .with("weeklySchedule[]", weeklySchedule)
        .build();
    action.run(submission);

    assertThat(submission.getInputData().get("displayWeeklySchedule")).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("org.ilgcc.app.submission.actions.SetClassWeeklyScheduleGroupTest#unsortedDays")
  void shouldSortWeekdays(List<String> weeklySchedule, String expected) {
    Submission submission = new SubmissionTestBuilder()
        .with("weeklySchedule[]", weeklySchedule)
        .build();
    action.run(submission);

    assertThat(submission.getInputData().get("displayWeeklySchedule")).isEqualTo(expected);
  }

  private static Stream<Arguments> consecutiveDaysArgs() {
    return Stream.of(
        Arguments.of(List.of("Monday", "Tuesday", "Wednesday"), "Monday-Wednesday"),
        Arguments.of(List.of("Wednesday", "Thursday", "Friday", "Saturday"), "Wednesday-Saturday"),
        Arguments.of(List.of("Friday", "Saturday", "Sunday"), "Friday-Sunday"),
        Arguments.of(List.of("Tuesday", "Wednesday"), "Tuesday-Wednesday")
    );
  }

  private static Stream<Arguments> dispersedDaysArgs() {
    return Stream.of(
        Arguments.of(List.of("Monday", "Wednesday"), "Monday, Wednesday"),
        Arguments.of(List.of("Wednesday", "Thursday", "Saturday"), "Wednesday, Thursday, Saturday"),
        Arguments.of(List.of("Sunday"), "Sunday"),
        Arguments.of(List.of("Tuesday", "Sunday"), "Tuesday, Sunday")
    );
  }

  private static Stream<Arguments> unsortedDays() {
    return Stream.of(
        Arguments.of(List.of("Friday", "Wednesday"), "Wednesday, Friday"),
        Arguments.of(List.of("Wednesday", "Thursday", "Tuesday"), "Tuesday-Thursday"),
        Arguments.of(List.of("Sunday", "Saturday", "Friday"), "Friday-Sunday"),
        Arguments.of(List.of("Tuesday", "Monday"), "Monday-Tuesday")
    );
  }
}