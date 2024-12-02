package org.ilgcc.app.data;

import formflow.library.data.Submission;
import java.math.BigInteger;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, BigInteger> {

    boolean existsByStatusInAndProviderId(List<String> statuses, BigInteger providerId);
}
