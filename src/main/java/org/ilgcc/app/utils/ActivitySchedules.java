package org.ilgcc.app.utils;

import com.google.common.collect.ImmutableMap;

import java.time.LocalTime;
import java.util.List;

/**
 * Helper data classes for working with ILGCC activity schedules (e.g. school, work, child care).
 */
public abstract class ActivitySchedules {
    public record LocalTimeRange(LocalTime startTime, LocalTime endTime) { }

    public interface HourlySchedule {
        ImmutableMap<DayOfWeekOption, LocalTimeRange> toDailyScheduleMap();
    }

    public record ConsistentHourlySchedule(
            List<DayOfWeekOption> weekdays, LocalTimeRange scheduleEveryDay) implements HourlySchedule {
        @Override
        public ImmutableMap<DayOfWeekOption, LocalTimeRange> toDailyScheduleMap() {
            ImmutableMap.Builder<DayOfWeekOption, LocalTimeRange> map = ImmutableMap.builder();
            for (DayOfWeekOption day : weekdays) {
                map.put(day, scheduleEveryDay);
            }
            return map.build();
        }
    }

    public record PerDayHourlySchedule(
            ImmutableMap<DayOfWeekOption, LocalTimeRange> dailyScheduleMap
    ) implements HourlySchedule {
        @Override
        public ImmutableMap<DayOfWeekOption, LocalTimeRange> toDailyScheduleMap() {
            return dailyScheduleMap;
        }
    }
}
