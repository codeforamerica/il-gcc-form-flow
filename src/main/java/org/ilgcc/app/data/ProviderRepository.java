package formflow.library.data;

import java.math.BigInteger;
import org.ilgcc.app.data.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, BigInteger> {

}
