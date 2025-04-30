package org.ilgcc.app.data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Jobrunr doesn't provide a way to directly access Jobs and the table structure. This Repository allows us to directly query
 * the Jobrunr tables as needed
 */
@Slf4j
@Repository
public class JobrunrJobRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<Instant> findLatestSuccessfulNoProviderResponseJobRunTime() {

        String sql = "SELECT updatedat FROM jobrunr_jobs j "
                + "WHERE j.recurringJobId = 'no-provider-response-job' AND j.state = 'SUCCEEDED' "
                + "order by j.updatedAt DESC "
                + "LIMIT 1";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Object timestampObj = rs.getObject("updatedat");
            if (timestampObj instanceof Timestamp timestamp) {
                // Jobrunr doesn't store the dates in a format with a timezone, but we know these are always UTC
                String utcDateString = timestamp.toString().replace(" ", "T") + "Z";
                return Instant.parse(utcDateString);
            }

            return null;  // Return null if no value is found
        }).stream().findFirst();
    }

    public int getScheduledCCMSJobCount() {
        String sql = "SELECT COUNT(id) FROM jobrunr_jobs j " +
                "WHERE j.scheduledAt IS NOT NULL " +
                "AND j.state = ? " +
                "AND j.jobSignature LIKE ?";

        Integer result = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                "SCHEDULED", "%sendCCMSTransaction%"
        );

        return result != null ? result : 0;
    }
}