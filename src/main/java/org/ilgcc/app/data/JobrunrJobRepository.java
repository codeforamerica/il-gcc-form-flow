package org.ilgcc.app.data;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobrunrJobRepository extends JpaRepository<JobrunrJob, String> {

    @Query("SELECT j FROM JobrunrJob j "
            + "WHERE j.recurringJobId = 'no-provider-response-job' AND j.state = 'SUCCEEDED' "
            + "order by j.updatedAt DESC "
            + "LIMIT 1")
    Optional<JobrunrJob> findLatestSuccessfulNoProviderResponseJob();
}