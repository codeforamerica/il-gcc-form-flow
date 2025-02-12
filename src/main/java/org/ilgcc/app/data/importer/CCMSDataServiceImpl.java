package org.ilgcc.app.data.importer;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.CCMSDataService;
import org.ilgcc.app.data.County;
import org.ilgcc.app.data.CountyRepository;
import org.ilgcc.app.data.Provider;
import org.ilgcc.app.data.ProviderRepository;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.data.ResourceOrganizationRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CCMSDataServiceImpl implements CCMSDataService {

    private final ProviderRepository providerRepository;
    private final CountyRepository countyRepository;
    private final ResourceOrganizationRepository resourceOrganizationRepository;

    public CCMSDataServiceImpl(ProviderRepository providerRepository, CountyRepository countyRepository, ResourceOrganizationRepository resourceOrganizationRepository) {
        this.providerRepository = providerRepository;
        this.countyRepository = countyRepository;
        this.resourceOrganizationRepository = resourceOrganizationRepository;
    }

    @Override
    public Optional<County> getCountyByZipCode(String zipCode) {
        try {
            final String truncatedZip = zipCode.substring(0, 5);
            final BigInteger zipCodeInt = new BigInteger(truncatedZip);
            return countyRepository.findByZipCode(zipCodeInt);
        } catch (Exception e) {
            log.error(String.format("Could not map the zip code (%s) to a county", zipCode));
            return Optional.empty();
        }
    }

    @Override
    public Optional<Provider> getProviderById(BigInteger providerId) {
        return providerRepository.findById(providerId);
    }

    @Override
    public Optional<ResourceOrganization> getResourceOrganizationByProviderId(BigInteger providerId) {
       return resourceOrganizationRepository.findByProvidersProviderId(providerId);
    }

    @Override
    public List<ResourceOrganization> getResourceOrganizationsByCaseloadCode(String caseloadCode) {
        return resourceOrganizationRepository.findByCaseloadCode(caseloadCode);
    }
}
