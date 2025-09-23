package org.ilgcc.app.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.*;

class UtcTimeUtilsTest {

    @Test
    void toUtc_fromZonedDateTime() {
        ZonedDateTime chicago = ZonedDateTime.of(2023, 3, 10, 10, 30, 0, 0, ZoneId.of("America/Chicago"));
        ZonedDateTime utc = UtcTimeUtils.toUtc(chicago);
        assertEquals(ZoneOffset.UTC, utc.getZone());
        assertEquals(chicago.toInstant(), utc.toInstant());
    }

    @Test
    void toUtc_fromInstant() {
        Instant now = Instant.now();
        ZonedDateTime utc = UtcTimeUtils.toUtc(now);
        assertEquals(now, utc.toInstant());
        assertEquals(ZoneOffset.UTC, utc.getZone());
    }

    @Test
    void toUtc_fromLocalDateTimeSystemDefault() {
        LocalDateTime ldt = LocalDateTime.of(2023, 11, 5, 2, 0); // DST edge case
        ZonedDateTime utc = UtcTimeUtils.toUtc(ldt);
        assertEquals(ldt.atZone(ZoneId.systemDefault()).toInstant(), utc.toInstant());
        assertEquals(ZoneOffset.UTC, utc.getZone());
    }

    @Test
    void fromUtc_toTargetZone() {
        ZonedDateTime utc = ZonedDateTime.of(2023, 6, 1, 15, 0, 0, 0, ZoneOffset.UTC);
        ZoneId tokyoZone = ZoneId.of("Asia/Tokyo");
        ZonedDateTime tokyo = UtcTimeUtils.fromUtc(utc, tokyoZone);
        assertEquals(tokyoZone, tokyo.getZone());
        assertEquals(utc.toInstant(), tokyo.toInstant());
    }

    @Test
    void fromUtc_instant_toTargetZone() {
        Instant instant = Instant.parse("2023-09-01T12:00:00Z");
        ZoneId berlinZone = ZoneId.of("Europe/Berlin");
        ZonedDateTime berlin = UtcTimeUtils.fromUtc(instant, berlinZone);
        assertEquals(berlinZone, berlin.getZone());
        assertEquals(instant, berlin.toInstant());
    }

    @Test
    void formatUtc_and_parseIsoString() {
        Instant now = Instant.now();
        String iso = UtcTimeUtils.formatUtc(now);
        Instant parsed = UtcTimeUtils.parseIsoString(iso);
        assertEquals(now.getEpochSecond(), parsed.getEpochSecond()); // allow for millis rounding
    }

    @Test
    void formatAsIsoUtc_fromZonedDateTime() {
        ZonedDateTime sydney = ZonedDateTime.of(2023, 7, 1, 20, 45, 0, 0, ZoneId.of("Australia/Sydney"));
        String iso = UtcTimeUtils.formatAsIsoUtc(sydney);
        Instant parsed = Instant.parse(iso);
        assertEquals(sydney.toInstant(), parsed);
        assertTrue(iso.endsWith("Z"));
    }

    @Test
    void parseUtcIsoToZone() {
        String iso = "2023-09-01T12:00:00Z";
        ZoneId laZone = ZoneId.of("America/Los_Angeles");
        ZonedDateTime la = UtcTimeUtils.parseUtcIsoToZone(iso, laZone);
        assertEquals(laZone, la.getZone());
        assertEquals(Instant.parse(iso), la.toInstant());
    }

    @Test
    void nullInputsThrowNPE() {
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.toUtc((ZonedDateTime) null));
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.toUtc((Instant) null));
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.toUtc((LocalDateTime) null));
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.fromUtc((ZonedDateTime) null, ZoneId.of("UTC")));
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.fromUtc(ZonedDateTime.now(), null));
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.fromUtc((Instant) null, ZoneId.of("UTC")));
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.fromUtc(Instant.now(), null));
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.formatUtc(null));
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.parseIsoString(null));
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.formatAsIsoUtc(null));
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.parseUtcIsoToZone(null, ZoneId.of("UTC")));
        assertThrows(NullPointerException.class, () -> UtcTimeUtils.parseUtcIsoToZone("2023-09-01T12:00:00Z", null));
    }
}