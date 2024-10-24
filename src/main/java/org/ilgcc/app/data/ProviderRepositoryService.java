package formflow.library.data;

import java.math.BigInteger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProviderRepositoryService {

    ProviderRepository providerRepository;

    public ProviderRepositoryService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public boolean doProvidersExist() {
        return !providerRepository.findAll().isEmpty();
    }

    public boolean isProviderIdValid(BigInteger providerId) {
        return providerRepository.findById(providerId).isPresent();
    }
}
