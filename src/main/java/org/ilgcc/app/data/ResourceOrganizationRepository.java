package org.ilgcc.app.data;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceOrganizationRepository extends JpaRepository<ResourceOrganization, BigInteger> {

    List<ResourceOrganization> findByCaseloadCode(String caseloadCode);

    Optional<ResourceOrganization> findByProvidersProviderId(BigInteger providerId);

    @Query("""
            SELECT r FROM Provider p
               JOIN ResourceOrganization r
               ON p.resourceOrganization = r
               where r.caseloadCode = 'SITE'
               and p.providerId = :providerId
               and r.sda in :activeSDAs
            """)
    Optional<ResourceOrganization> findActiveSiteAdministeredOrgByProviderId(@Param("providerId") BigInteger providerId,
            @Param("activeSDAs") List<Short> activeSDAs);

}
