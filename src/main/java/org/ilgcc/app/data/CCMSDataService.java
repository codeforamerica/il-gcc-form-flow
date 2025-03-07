package org.ilgcc.app.data;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface CCMSDataService {

    /**
     * Retrieves a county based on the given zip code.
     *
     * @param zipCode the zip code to search for
     * @return an Optional containing the matching county if found, or an empty Optional if not found
     */
    Optional<County> getCountyByZipCode(String zipCode);

    /**
     * Retrieves a county based on the given county name.
     *
     * @param countyName the county we are searching for
     * @return a List of Counties containing the matching counties if found, or an empty list not found
     */
    List<County> getCountyByCountyName(String countyName);

    /**
     * Retrieves a provider based on the given provider ID.
     *
     * @param providerId the unique identifier of the provider
     * @return an Optional containing the provider if found, or an empty Optional if not found
     */
    Optional<Provider> getProviderById(BigInteger providerId);

    /**
     * Retrieves a site administered resource organization associated with a given provider ID.
     *
     * @param providerId the unique identifier of the provider
     * @return an Optional containing the matching resource organization if found, or an empty Optional if not found
     */
    Optional<ResourceOrganization> getSiteAdministeredResourceOrganizationByProviderId(BigInteger providerId);

    /**
     * Retrieves a list of resource organizations based on the given caseload code.
     *
     * @param caseloadCode the caseload code to search for
     * @return a list of matching resource organizations
     */
    List<ResourceOrganization> getResourceOrganizationsByCaseloadCode(String caseloadCode);

    Optional<List<County>> getCountiesByCaseloadCode(String caseloadCode);
}

