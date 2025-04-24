package org.ilgcc.app.submission.router;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.ilgcc.app.data.CCMSDataService;
import org.ilgcc.app.data.County;
import org.ilgcc.app.data.ResourceOrganization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationRoutingServiceImpl implements ApplicationRouterService {

    private final CCMSDataService ccmsDataService;

    public List<String> activeCaseLoadCodes;

    @Autowired
    public ApplicationRoutingServiceImpl(CCMSDataService ccmsDataService) {
        this.ccmsDataService = ccmsDataService;
        this.activeCaseLoadCodes = ccmsDataService.getActiveCaseLoadCodes();
    }

    @Override
    public Optional<ResourceOrganization> getOrganizationIdByZipCode(String zipCode) {
        if (zipCode == null || zipCode.length() < 5) {
            return Optional.empty();
        }
        final String truncatedZip = zipCode.substring(0, 5);
        Optional<County> countyByZipCode = ccmsDataService.getCountyByZipCode(truncatedZip);
        if (countyByZipCode.isPresent() && activeCaseLoadCodes.contains(countyByZipCode.get().getCaseloadCode())) {
            List<ResourceOrganization> resourceOrganizationsByCaseloadCode = ccmsDataService.getResourceOrganizationsByCaseloadCode(
                    countyByZipCode.get().getCaseloadCode());
            return resourceOrganizationsByCaseloadCode.stream().filter((r) -> !r.getCaseloadCode().equals("SITE")).findFirst();
        }
        return Optional.empty();
    }

    @Override
    public Optional<ResourceOrganization> getOrganizationByCountyName(String countyName) {
        if (countyName == null || countyName.isBlank()) {
            return Optional.empty();
        }
        List<County> counties = ccmsDataService.getCountyByCountyName(countyName);
        Optional<County> activeCounty = counties.stream()
                .filter((c) -> c != null && activeCaseLoadCodes.contains(c.getCaseloadCode())).findFirst();
        if (activeCounty.isPresent()) {
            List<ResourceOrganization> resourceOrganizationsByCaseloadCode = ccmsDataService.getResourceOrganizationsByCaseloadCode(
                    activeCounty.get().getCaseloadCode());
            return resourceOrganizationsByCaseloadCode.stream().filter((r) -> !r.getCaseloadCode().equals("SITE")).findFirst();
        }
        return Optional.empty();
    }

    @Override
    public Optional<ResourceOrganization> getSiteAdministeredOrganizationByProviderId(BigInteger providerId) {
        return ccmsDataService.getSiteAdministeredResourceOrganizationByProviderId(
                providerId, ccmsDataService.getActiveSDAsBasedOnActiveCaseLoadCodes());
    }

    @Override
    public List<County> getActiveCountiesByCaseLoadCodes() {
        List<County> counties = new ArrayList<>();
        Set<String> countyNames = new HashSet<>(); // To track unique county names

        for (String code : activeCaseLoadCodes) {
            List<County> countiesConnectedToThisCaseloadCode = ccmsDataService.getCountiesByCaseloadCode(code);

            for (County currentCounty : countiesConnectedToThisCaseloadCode) {
                if (countyNames.add(currentCounty.getCounty().toLowerCase())) {
                    counties.add(currentCounty);
                }
            }
        }

        counties.sort(Comparator.comparing(county -> county.getCounty().toLowerCase()));

        return counties;
    }
}
