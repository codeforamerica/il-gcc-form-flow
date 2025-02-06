package org.ilgcc.app.data;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, BigInteger> {

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM providers "
            + "WHERE status IN :statuses AND provider_id = :providerId "
            + "AND date_of_last_approval >= :threeYearsAgo", nativeQuery = true)
    boolean existsByStatusInAndProviderIdAndDateOfLastApprovalAfter(@Param("statuses") List<String> statuses,
            @Param("providerId") BigInteger providerId, @Param("threeYearsAgo") LocalDate threeYearsAgo);

}
