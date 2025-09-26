package org.ilgcc.app.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class UtcTimeUtils {

    public static final ZoneId UTC = ZoneOffset.UTC;
    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public static ZonedDateTime toUtc(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime cannot be null");
        return zonedDateTime.withZoneSameInstant(UTC);
    }

    public static ZonedDateTime toUtc(Instant instant) {
        Objects.requireNonNull(instant, "instant cannot be null");
        return instant.atZone(UTC);
    }

    public static ZonedDateTime toUtc(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime cannot be null");
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC);
    }

    public static ZonedDateTime fromUtc(ZonedDateTime utcZonedDateTime, ZoneId targetZone) {
        Objects.requireNonNull(utcZonedDateTime, "utcZonedDateTime cannot be null");
        Objects.requireNonNull(targetZone, "targetZone cannot be null");
        return utcZonedDateTime.withZoneSameInstant(targetZone);
    }

    public static ZonedDateTime fromUtc(Instant utcInstant, ZoneId targetZone) {
        Objects.requireNonNull(utcInstant, "utcInstant cannot be null");
        Objects.requireNonNull(targetZone, "targetZone cannot be null");
        return utcInstant.atZone(targetZone);
    }

    public static String formatUtc(Instant instant) {
        Objects.requireNonNull(instant, "instant cannot be null");
        return ISO_FORMATTER.format(instant);
    }

    public static Instant parseIsoString(String isoString) {
        Objects.requireNonNull(isoString, "isoString cannot be null");
        return Instant.parse(isoString);
    }

    public static String formatAsIsoUtc(ZonedDateTime zonedDateTime) {
        return formatUtc(toUtc(zonedDateTime).toInstant());
    }

    public static ZonedDateTime parseUtcIsoToZone(String isoString, ZoneId targetZone) {
        Instant instant = parseIsoString(isoString);
        return instant.atZone(targetZone);
    }
}

