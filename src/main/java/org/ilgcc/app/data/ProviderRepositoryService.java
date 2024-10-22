package org.ilgcc.app.data;

import jakarta.transaction.Transactional;
import java.math.BigInteger;
import org.springframework.stereotype.Service;

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
