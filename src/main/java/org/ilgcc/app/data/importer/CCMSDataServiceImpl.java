package org.ilgcc.app.data.importer;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.ilgcc.app.data.CCMSDataService;
import org.ilgcc.app.data.County;
import org.ilgcc.app.data.CountyRepository;
import org.ilgcc.app.data.Provider;
import org.ilgcc.app.data.ProviderRepository;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.data.ResourceOrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CCMSDataServiceImpl implements CCMSDataService {

    private final ProviderRepository providerRepository;
    private final CountyRepository countyRepository;
    private final ResourceOrganizationRepository resourceOrganizationRepository;

    @Autowired
    public CCMSDataServiceImpl(ProviderRepository providerRepository, CountyRepository countyRepository, ResourceOrganizationRepository resourceOrganizationRepository) {
        this.providerRepository = providerRepository;
        this.countyRepository = countyRepository;
        this.resourceOrganizationRepository = resourceOrganizationRepository;
    }

    @Override
    public Optional<County> getCountyByZipCode(String zipCode) {
        return countyRepository.findById(zipCode);
    }

    @Override
    public Optional<Provider> getProviderById(BigInteger providerId) {
        return providerRepository.findById(providerId);
    }

    @Override
    public Optional<ResourceOrganization> getResourceOrganizationByProviderId(BigInteger providerId) {
       return resourceOrganizationRepository.findByProvidersId(providerId);
    }

    @Override
    public List<ResourceOrganization> getResourceOrganizationsByCaseloadCode(String caseloadCode) {
        return resourceOrganizationRepository.findByCaseloadCode(caseloadCode);
    }
}
