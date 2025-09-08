package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import formflow.library.data.Submission;
import java.util.List;
import java.util.stream.Stream;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

class SetClassWeeklyScheduleGroupTest {

  private SetClassWeeklyScheduleGroup action;

  @BeforeEach
  void setup() {
    MessageSource messageSource = Mockito.mock(MessageSource.class);
    action = new SetClassWeeklyScheduleGroup(messageSource);

    Stream.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        .forEach(d -> when(messageSource.getMessage(eq("general.week." + d), any(), any())).thenReturn(d));

      when(messageSource.getMessage(eq("general.week.day-range"), any(), any()))
              .thenAnswer(methodCalled -> {
                  Object[] args = methodCalled.getArgument(1, Object[].class);
                  return args[0] + " through " + args[1]; // e.g., "Monday through Friday"
              });
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
        Arguments.of(List.of("Monday", "Tuesday", "Wednesday"), "Monday through Wednesday"),
        Arguments.of(List.of("Wednesday", "Thursday", "Friday", "Saturday"), "Wednesday through Saturday"),
        Arguments.of(List.of("Friday", "Saturday", "Sunday"), "Friday through Sunday"),
        Arguments.of(List.of("Tuesday", "Wednesday"), "Tuesday through Wednesday")
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
        Arguments.of(List.of("Wednesday", "Thursday", "Tuesday"), "Tuesday through Thursday"),
        Arguments.of(List.of("Sunday", "Saturday", "Friday"), "Friday through Sunday"),
        Arguments.of(List.of("Tuesday", "Monday"), "Monday through Tuesday")
    );
  }
}