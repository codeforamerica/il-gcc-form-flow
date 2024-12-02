package org.ilgcc.app.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProviderRepositoryService {

    ProviderRepository providerRepository;

    private static final String STATUS_ACTIVE = "A";
    private static final String STATUS_PENDING = "P";
    private static final List<String> VALID_STATUSES = List.of(STATUS_ACTIVE, STATUS_PENDING);

    public ProviderRepositoryService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public boolean isProviderIdValid(String providerId) {

        if (!Objects.isNull(providerId) && !providerId.isEmpty()) {
            try {
                BigInteger providerIdNumber = new BigInteger(providerId);
                if (!providerRepository.existsByStatusInAndProviderId(VALID_STATUSES, providerIdNumber)) {
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
