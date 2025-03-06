package org.ilgcc.app.submission.router;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.ilgcc.app.data.County;
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
     * Returns the site administered resource organization's Id for a given provider Id
     *
     * @param providerId The numeric provider Id to look up
     * @return The site administered resource organization's Id
     */
    Optional<BigInteger> getSiteAdministeredOrganizationIdByProviderId(BigInteger providerId);

    Optional<List<County>> getCountiesByCaseLoadCode(String caseLoadCode);
}
