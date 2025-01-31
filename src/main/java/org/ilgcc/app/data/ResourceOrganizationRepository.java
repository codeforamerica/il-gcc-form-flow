package org.ilgcc.app.data;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceOrganizationRepository extends JpaRepository<ResourceOrganization, Long> {

    List<ResourceOrganization> findByCaseloadCode(String caseloadCode);

    Optional<ResourceOrganization> findByProvidersId(BigInteger providerId);
}
