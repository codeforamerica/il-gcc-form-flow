package org.ilgcc.app.submission.router;

import java.math.BigInteger;
import java.util.Optional;
import org.ilgcc.app.data.ResourceOrganization;
import org.springframework.stereotype.Service;

@Service

public interface ApplicationRouterService {

    /**
     * Returns the organization ID for a given ZIP code
     *
     * @param zipCode The ZIP code to look up
     * @return The corresponding organization ID
     */
    Optional<ResourceOrganization> getOrganizationIdByZipCode(String zipCode);

    /**
     * Returns the organization ID for a given county name
     *
     * @param countyName The county name to look up
     * @return The corresponding organization ID
     */
    Optional<ResourceOrganization> getOrganizationByCountyName(String countyName);

    /**
     * Returns the organization ID for a given provider ID
     *
     * @param providerId The alphanumeric provider ID to look up
     * @return The corresponding organization ID
     */
    Optional<BigInteger> getOrganizationIdByProviderId(BigInteger providerId);
}
