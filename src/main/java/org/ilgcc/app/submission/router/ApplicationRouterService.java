package org.ilgcc.app.submission.router;

import java.util.Optional;
import org.springframework.stereotype.Service;

@Service

public interface ApplicationRouterService {

    /**
     * Returns the organization ID for a given ZIP code
     *
     * @param zipCode The ZIP code to look up
     * @return The corresponding organization ID
     */
    Optional<String> getOrganizationIdByZipCode(String zipCode);

    /**
     * Returns the organization ID for a given provider ID
     *
     * @param providerId The alphanumeric provider ID to look up
     * @return The corresponding organization ID
     */
    Optional<String> getOrganizationIdByProviderId(String providerId);
}
