package org.ilgcc.app.data;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceOrganizationRepository extends JpaRepository<ResourceOrganization, BigInteger> {

    List<ResourceOrganization> findByCaseloadCode(String caseloadCode);

    Optional<ResourceOrganization> findByProvidersProviderId(BigInteger providerId);
}
