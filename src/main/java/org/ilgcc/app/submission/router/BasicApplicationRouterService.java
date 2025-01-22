package org.ilgcc.app.submission.router;

import java.util.Optional;
import org.ilgcc.app.utils.ZipcodeOption;
import org.springframework.stereotype.Service;

@Service
public class BasicApplicationRouterService implements ApplicationRouterService {


    @Override
    public Optional<String> getOrganizationIdByZipCode(String zipCode) {
        final String truncatedZip = zipCode.substring(0, 5);
        return ZipcodeOption.getOrganizationIdByZipCode(truncatedZip);
    }

    @Override
    public Optional<String> getOrganizationIdByProviderId(String providerId) {
        return Optional.empty();
    }
}
