package org.ilgcc.app.data;

import java.math.BigInteger;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProviderRepositoryService {

    ProviderRepository providerRepository;

    public ProviderRepositoryService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public boolean isProviderIdValid(String providerId) {

        if (!Objects.isNull(providerId) && !providerId.isEmpty()) {
            try {
                BigInteger providerIdNumber = new BigInteger(providerId);
                if (!providerRepository.existsById(providerIdNumber)) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
