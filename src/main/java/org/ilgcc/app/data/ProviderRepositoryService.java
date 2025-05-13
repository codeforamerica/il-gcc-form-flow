package org.ilgcc.app.data;

import java.math.BigInteger;
import java.time.LocalDate;
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
                return providerRepository.existsByStatusInAndProviderIdAndDateOfLastApprovalAfter(VALID_STATUSES,
                        new BigInteger(providerId), LocalDate.now().minusYears(3));
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isFEINValid(String fein) {
        if (!Objects.isNull(fein) && !fein.isEmpty()) {
            try {
                return providerRepository.existsByStatusInAndFEINAndDateOfLastApprovalAfter(VALID_STATUSES,
                        new BigInteger(fein.replaceAll("\\D", "")), LocalDate.now().minusYears(3));
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
