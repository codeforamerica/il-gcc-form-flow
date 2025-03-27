package org.ilgcc.app.utils;
import static org.junit.jupiter.api.Assertions.*;
import java.time.OffsetDateTime;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class DateUtilitiesTest {

  @Test
  void testGetMonthStringFromOffsetDateTime() {
    OffsetDateTime dateTime = OffsetDateTime.parse("2021-04-27T10:15:30Z");
    String expected = "April";
    assertEquals(expected, DateUtilities.getMonthStringFromOffsetDateTime(dateTime, Locale.ENGLISH));
  }

  @Test
  void testGetDayFromOffsetDateTime() {
    OffsetDateTime dateTime = OffsetDateTime.parse("2024-03-10T10:15:30Z");
    String expected = "10";
    assertEquals(expected, DateUtilities.getDayFromOffsetDateTime(dateTime));
  }

  @Test
  void testGetTimeFromOffsetDateTime() {
    OffsetDateTime dateTime = OffsetDateTime.parse("2024-03-27T15:30:00Z"); // 10:30 AM CST
    String expected = "10:30";
    assertEquals(expected, DateUtilities.getTimeFromOffsetDateTime(dateTime));
  }
}
