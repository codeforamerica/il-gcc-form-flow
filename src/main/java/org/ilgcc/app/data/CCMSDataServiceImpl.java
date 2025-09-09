package org.ilgcc.app.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CCMSDataServiceImpl implements CCMSDataService {

    private final ProviderRepository providerRepository;
    private final CountyRepository countyRepository;
    private final ResourceOrganizationRepository resourceOrganizationRepository;
    private final List<String> activeCaseLoadCodes = new ArrayList<>();

    private final Map<String, Short> caseLoadCodeToSDA = Map.ofEntries(
            Map.entry("AA", (short) 1),
            Map.entry("BB", (short) 2),
            Map.entry("CC", (short) 3),
            Map.entry("EE", (short) 4),
            Map.entry("FF", (short) 5),
            Map.entry("GG", (short) 6),
            Map.entry("HH", (short) 7),
            Map.entry("II", (short) 8),
            Map.entry("JJ", (short) 9),
            Map.entry("KK", (short) 10),
            Map.entry("LL", (short) 11),
            Map.entry("MM", (short) 12),
            Map.entry("NN", (short) 13),
            Map.entry("PP", (short) 14),
            Map.entry("QQ", (short) 15),
            Map.entry("RR", (short) 16)
    );

    public CCMSDataServiceImpl(ProviderRepository providerRepository, CountyRepository countyRepository,
            ResourceOrganizationRepository resourceOrganizationRepository) {
        this.providerRepository = providerRepository;
        this.countyRepository = countyRepository;
        this.resourceOrganizationRepository = resourceOrganizationRepository;
    }

    @Override
    public List<String> getActiveCaseLoadCodes() {
        return activeCaseLoadCodes;
    }

    @Override
    public List<Short> getActiveSDAsBasedOnActiveCaseLoadCodes() {
        List<Short> activeSDAs = new ArrayList<>();
        activeCaseLoadCodes.stream().forEach(c -> {
            activeSDAs.add(caseLoadCodeToSDA.get(c));
        });

        return activeSDAs;
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
    public List<County> getCountyByCountyName(String countyName) {
        try {
            return countyRepository.findByCounty(countyName);
        } catch (Exception e) {
            log.error(String.format("Could not find a County with the county name (%s)", countyName));
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Provider> getProviderById(BigInteger providerId) {
        return providerRepository.findById(providerId);
    }

    @Override
    public Optional<ResourceOrganization> getSiteAdministeredResourceOrganizationByProviderId(BigInteger providerId,
            List<Short> activeSDAs) {
        return resourceOrganizationRepository.findActiveSiteAdministeredOrgByProviderId(providerId, activeSDAs);
    }

    @Override
    public List<ResourceOrganization> getResourceOrganizationsByCaseloadCode(String caseloadCode) {
        return resourceOrganizationRepository.findByCaseloadCode(caseloadCode);
    }

    @Override
    public List<ResourceOrganization> getActiveResourceOrganizations() {
        return resourceOrganizationRepository.findAll().stream()
                .filter(t -> {
                    String code = t.getCaseloadCode();
                    if ("SITE".equals(code)) {
                        return getActiveSDAsBasedOnActiveCaseLoadCodes().contains(t.getSda());
                    } else {
                        return activeCaseLoadCodes.contains(code);
                    }
                })
                .toList();
    }

    @Override
    public List<County> getCountiesByCaseloadCode(String caseloadCode) {
        return countyRepository.findCountiesByCaseloadCode(caseloadCode);
    }
}
