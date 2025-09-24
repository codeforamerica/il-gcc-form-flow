package org.ilgcc.app.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CCMSApiConfigurationTest {

    CCMSApiConfiguration ccmsApiConfiguration;

    @BeforeEach
    public void setUp() {
        ccmsApiConfiguration = new CCMSApiConfiguration();

        ccmsApiConfiguration.setApiSubscriptionKey("test");
        ccmsApiConfiguration.setBaseUrl("http://localhost:8080");

        ccmsApiConfiguration.setTransactionDelayMinutes(60);

        ccmsApiConfiguration.setOfflineTransactionDelayOffset(15);
        ccmsApiConfiguration.setOfflineTimeRanges("[]");
        ccmsApiConfiguration.setIntegrationEnabled(true);

        ccmsApiConfiguration.init();
    }

    @Test
    public void sampleConfiguration() {
        assertThat(ccmsApiConfiguration.getApiSubscriptionKey()).isEqualTo("test");
        assertThat(ccmsApiConfiguration.getBaseUrl()).isEqualTo("http://localhost:8080");
        assertThat(ccmsApiConfiguration.getTransactionDelayMinutes()).isEqualTo(60);
        assertThat(ccmsApiConfiguration.getOfflineTransactionDelayOffset()).isEqualTo(15);
        assertThat(ccmsApiConfiguration.getOfflineTimeRanges()).isEqualTo("[]");
        assertThat(ccmsApiConfiguration.getCcmsOfflineTimeRanges().isEmpty()).isTrue();
        assertThat(ccmsApiConfiguration.isCCMSIntegrationEnabled()).isTrue();
    }

    @Test
    public void defaultCCMSOfflineConfiguration() {
        // By default, we'll want to have CCMS off from 11pm - 5am.
        ccmsApiConfiguration.setOfflineTimeRanges("[{\"start\": \"23:00\", \"end\": \"05:00\"}]");
        ccmsApiConfiguration.init();

        assertThat(ccmsApiConfiguration.getApiSubscriptionKey()).isEqualTo("test");
        assertThat(ccmsApiConfiguration.getBaseUrl()).isEqualTo("http://localhost:8080");
        assertThat(ccmsApiConfiguration.getTransactionDelayMinutes()).isEqualTo(60);
        assertThat(ccmsApiConfiguration.getOfflineTransactionDelayOffset()).isEqualTo(15);

        assertThat(ccmsApiConfiguration.getOfflineTimeRanges()).isEqualTo("[{\"start\": \"23:00\", \"end\": \"05:00\"}]");
        assertThat(ccmsApiConfiguration.getCcmsOfflineTimeRanges().size() == 1).isTrue();

        ZoneId zoneId = ZoneId.of("America/Chicago");
        ZonedDateTime today = ZonedDateTime.now(zoneId);

        // 9:30am - ONLINE
        today = today.withHour(9).withMinute(30).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isTrue();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo(0);

        // 9:30pm - ONLINE
        today = today.withHour(21).withMinute(30).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isTrue();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo(0);

        // 10:59:59pm - ONLINE
        today = today.withHour(22).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isTrue();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo(0);

        // 11:00:00pm - OFFLINE
        today = today.withHour(23).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo(6 * 60 * 60);

        // 11:00:01pm - OFFLINE
        today = today.withHour(23).withMinute(0).withSecond(1).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo((6 * 60 * 60) - 1);

        // 11:59:59pm - OFFLINE
        today = today.withHour(23).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo(1 + (5 * 60 * 60));

        // Tomorrow 12:00:00am - OFFLINE
        ZonedDateTime tomorrow = today.plusDays(1);
        tomorrow = tomorrow.withHour(0).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(tomorrow)).isEqualTo((5 * 60 * 60));

        // Tomorrow 4:59:59am - OFFLINE
        tomorrow = tomorrow.withHour(4).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(tomorrow)).isEqualTo(1);

        // Tomorrow 5:00:00am - OFFLINE
        tomorrow = tomorrow.withHour(5).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(tomorrow)).isEqualTo(0);

        // Tomorrow 5:00:01am - ONLINE
        tomorrow = tomorrow.withHour(5).withMinute(0).withSecond(1).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isTrue();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(tomorrow)).isEqualTo(0);

        // Tomorrow 11:00:01pm - OFFLINE
        tomorrow = tomorrow.withHour(23).withMinute(0).withSecond(1).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(tomorrow)).isEqualTo((6 * 60 * 60) - 1);
    }

    @Test
    public void defaultCCMSOfflineConfiguration_CCMS_disabled() {
        // By default, we'll want to have CCMS off from 11pm - 5am.
        ccmsApiConfiguration.setOfflineTimeRanges("[{\"start\": \"23:00\", \"end\": \"05:00\"}]");

        // For this test, CCMS integration is overridden and disabled!
        ccmsApiConfiguration.setIntegrationEnabled(false);
        ccmsApiConfiguration.init();

        assertThat(ccmsApiConfiguration.getApiSubscriptionKey()).isEqualTo("test");
        assertThat(ccmsApiConfiguration.getBaseUrl()).isEqualTo("http://localhost:8080");
        assertThat(ccmsApiConfiguration.getTransactionDelayMinutes()).isEqualTo(60);
        assertThat(ccmsApiConfiguration.getOfflineTransactionDelayOffset()).isEqualTo(15);

        assertThat(ccmsApiConfiguration.getOfflineTimeRanges()).isEqualTo("[{\"start\": \"23:00\", \"end\": \"05:00\"}]");
        assertThat(ccmsApiConfiguration.getCcmsOfflineTimeRanges().size() == 1).isTrue();

        ZoneId zoneId = ZoneId.of("America/Chicago");
        ZonedDateTime today = ZonedDateTime.now(zoneId);

        // 9:30am - CCMS should be ONLINE, but it's overridden to be completely disabled
        today = today.withHour(9).withMinute(30).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo(3600);

        // 9:30pm - CCMS should be ONLINE, but it's overridden to be completely disabled
        today = today.withHour(21).withMinute(30).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo(3600);

        // 10:59:59pm - CCMS should be ONLINE, but it's overridden to be completely disabled
        today = today.withHour(22).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo(3600);

        // 11:00:00pm - OFFLINE due to the time range
        today = today.withHour(23).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo(6 * 60 * 60);

        // 11:00:01pm - OFFLINE due to the time range
        today = today.withHour(23).withMinute(0).withSecond(1).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo((6 * 60 * 60) - 1);

        // 11:59:59pm - OFFLINE due to the time range
        today = today.withHour(23).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(today)).isEqualTo(1 + (5 * 60 * 60));

        // Tomorrow 12:00:00am - OFFLINE due to the time range
        ZonedDateTime tomorrow = today.plusDays(1);
        tomorrow = tomorrow.withHour(0).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(tomorrow)).isEqualTo((5 * 60 * 60));

        // Tomorrow 4:59:59am - OFFLINE due to the time range
        tomorrow = tomorrow.withHour(4).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(tomorrow)).isEqualTo(1);

        // Tomorrow 5:00:00am - OFFLINE due to the time range
        tomorrow = tomorrow.withHour(5).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(tomorrow)).isEqualTo(0);

        // Tomorrow 5:00:01am - CCMS should be ONLINE, but it's overridden to be completely disabled
        tomorrow = tomorrow.withHour(5).withMinute(0).withSecond(1).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(tomorrow)).isEqualTo(3600);

        // Tomorrow 11:00:01pm - OFFLINE due to the time range
        tomorrow = tomorrow.withHour(23).withMinute(0).withSecond(1).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();
        assertThat(ccmsApiConfiguration.getSecondsUntilEndOfOfflineRangeStartingAt(tomorrow)).isEqualTo((6 * 60 * 60) - 1);
    }

    @Test
    public void twentyFourHourCCMSOfflineConfiguration() {
        // Off for the entire day
        ccmsApiConfiguration.setOfflineTimeRanges("[{\"start\": \"00:00\", \"end\": \"00:00\"}]");
        ccmsApiConfiguration.init();

        assertThat(ccmsApiConfiguration.getApiSubscriptionKey()).isEqualTo("test");
        assertThat(ccmsApiConfiguration.getBaseUrl()).isEqualTo("http://localhost:8080");
        assertThat(ccmsApiConfiguration.getTransactionDelayMinutes()).isEqualTo(60);
        assertThat(ccmsApiConfiguration.getOfflineTransactionDelayOffset()).isEqualTo(15);

        assertThat(ccmsApiConfiguration.getOfflineTimeRanges()).isEqualTo("[{\"start\": \"00:00\", \"end\": \"00:00\"}]");
        assertThat(ccmsApiConfiguration.getCcmsOfflineTimeRanges().size() == 1).isTrue();

        ZoneId zoneId = ZoneId.of("America/Chicago");
        ZonedDateTime today = ZonedDateTime.now(zoneId);

        // 9:30am - OFFLINE
        today = today.withHour(9).withMinute(30).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();

        // 9:30pm - OFFLINE
        today = today.withHour(21).withMinute(30).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();

        // 10:59:59pm - OFFLINE
        today = today.withHour(22).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();

        // 11:00:00pm - OFFLINE
        today = today.withHour(23).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();

        // 11:00:01pm - OFFLINE
        today = today.withHour(23).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();

        // 11:59:59pm - OFFLINE
        today = today.withHour(23).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();

        // Tomorrow 12:00:00am - OFFLINE
        ZonedDateTime tomorrow = today.plusDays(1);
        tomorrow = tomorrow.withHour(0).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();

        // Tomorrow 4:59:59am - OFFLINE
        tomorrow = tomorrow.withHour(4).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();

        // Tomorrow 5:00:00am - OFFLINE
        tomorrow = tomorrow.withHour(5).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();

        // Tomorrow 5:00:01am - OFFLINE
        tomorrow = tomorrow.withHour(5).withMinute(0).withSecond(1).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();

        // Tomorrow 11:00:01pm - OFFLINE
        tomorrow = tomorrow.withHour(23).withMinute(0).withSecond(1).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();
    }

    @Test
    public void multipleOutagesCCMSOfflineConfiguration() {
        // By default, we'll want to have CCMS off from 11pm - 5am. This will also test if there's an emergency outage and we're
        // also offline from 8am - 11am.
        ccmsApiConfiguration.setOfflineTimeRanges(
                "[{\"start\": \"23:00\", \"end\": \"05:00\"}, {\"start\": \"08:00\", \"end\": \"11:00\"}]");
        ccmsApiConfiguration.init();

        assertThat(ccmsApiConfiguration.getApiSubscriptionKey()).isEqualTo("test");
        assertThat(ccmsApiConfiguration.getBaseUrl()).isEqualTo("http://localhost:8080");
        assertThat(ccmsApiConfiguration.getTransactionDelayMinutes()).isEqualTo(60);
        assertThat(ccmsApiConfiguration.getOfflineTransactionDelayOffset()).isEqualTo(15);

        assertThat(ccmsApiConfiguration.getOfflineTimeRanges()).isEqualTo(
                "[{\"start\": \"23:00\", \"end\": \"05:00\"}, {\"start\": \"08:00\", \"end\": \"11:00\"}]");
        assertThat(ccmsApiConfiguration.getCcmsOfflineTimeRanges().size() == 2).isTrue();

        ZoneId zoneId = ZoneId.of("America/Chicago");
        ZonedDateTime today = ZonedDateTime.now(zoneId);

        // 7:30am - ONLINE
        today = today.withHour(7).withMinute(30).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isTrue();

        // 9:30am - OFFLINE
        today = today.withHour(9).withMinute(30).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();

        // 11:00:01am - ONLINE
        today = today.withHour(11).withMinute(0).withSecond(01).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isTrue();

        // 9:30pm - ONLINE
        today = today.withHour(21).withMinute(30).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isTrue();

        // 10:59:59pm - ONLINE
        today = today.withHour(22).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isTrue();

        // 11:00:00pm - OFFLINE
        today = today.withHour(23).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();

        // 11:00:01pm - OFFLINE
        today = today.withHour(23).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();

        // 11:59:59pm - OFFLINE
        today = today.withHour(23).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(today)).isFalse();

        // Tomorrow 12:00:00am - OFFLINE
        ZonedDateTime tomorrow = today.plusDays(1);
        tomorrow = tomorrow.withHour(0).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();

        // Tomorrow 4:59:59am - OFFLINE
        tomorrow = tomorrow.withHour(4).withMinute(59).withSecond(59).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();

        // Tomorrow 5:00:00am - OFFLINE
        tomorrow = tomorrow.withHour(5).withMinute(0).withSecond(0).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();

        // Tomorrow 5:00:01am - ONLINE
        tomorrow = tomorrow.withHour(5).withMinute(0).withSecond(1).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isTrue();

        // Tomorrow 11:00:01pm - OFFLINE
        tomorrow = tomorrow.withHour(23).withMinute(0).withSecond(1).withNano(0);
        assertThat(ccmsApiConfiguration.isOnlineAt(tomorrow)).isFalse();
    }
}
