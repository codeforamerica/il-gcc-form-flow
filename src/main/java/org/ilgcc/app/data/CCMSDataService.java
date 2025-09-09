package org.ilgcc.app.data;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface CCMSDataService {

    /**
     * Retrieves a list of active caseload codes
     *
     * @return an List containing a list of caseload code strings
     */

    List<String> getActiveCaseLoadCodes();

    /**
     * Retrieves a list of active SDAs based on active caseload codes
     *
     * @return a List<Short> of the Active SDAs
     */
    List<Short> getActiveSDAsBasedOnActiveCaseLoadCodes();

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
     * @param activeSDAs the sdas that are currently active
     * @return an Optional containing the matching resource organization if found, or an empty Optional if not found
     */
    Optional<ResourceOrganization> getSiteAdministeredResourceOrganizationByProviderId(BigInteger providerId,
            List<Short> activeSDAs);

    /**
     * Retrieves a list of resource organizations based on the given caseload code.
     *
     * @param caseloadCode the caseload code to search for
     * @return a list of matching resource organizations
     */
    List<ResourceOrganization> getResourceOrganizationsByCaseloadCode(String caseloadCode);

    /**
     * Retrieves a list of active resource organizations
     *
     * @return a list active resource organizations
     */
    List<ResourceOrganization> getActiveResourceOrganizations();

    /**
     * Retrieves a list of Counties by caseload code.
     *
     * @return a list of all counties
     */
    List<County> getAllCounties();
}

