package org.ilgcc.app.submission.router;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.ilgcc.app.data.CCMSDataService;
import org.ilgcc.app.data.County;
import org.ilgcc.app.data.ResourceOrganization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationRoutingServiceImpl implements ApplicationRouterService{
    private final List<String> activeCaseLoadCodes = List.of("BB", "QQ");
    private final CCMSDataService ccmsDataService;

    @Autowired
    public ApplicationRoutingServiceImpl(CCMSDataService ccmsDataService) {
        this.ccmsDataService = ccmsDataService;
    }

    @Override
    public Optional<ResourceOrganization> getOrganizationIdByZipCode(String zipCode) {
        if (zipCode == null || zipCode.length() < 5) {
            return Optional.empty();
        }
        final String truncatedZip = zipCode.substring(0, 5);
        Optional<County> countyByZipCode = ccmsDataService.getCountyByZipCode(truncatedZip);
        if (countyByZipCode.isPresent() && activeCaseLoadCodes.contains(countyByZipCode.get().getCaseloadCode())) {
            List<ResourceOrganization> resourceOrganizationsByCaseloadCode = ccmsDataService.getResourceOrganizationsByCaseloadCode(countyByZipCode.get().getCaseloadCode());
            return resourceOrganizationsByCaseloadCode.stream().filter((r) -> !r.getCaseloadCode().equals("SITE")).findFirst();
        }
        return Optional.empty();
    }

    @Override
    public Optional<ResourceOrganization> getOrganizationByCountyName(String countyName) {
        if(countyName == null || countyName.isBlank()){
            return Optional.empty();
        }
        List<County> counties = ccmsDataService.getCountyByCountyName(countyName);
        Optional<County> firstCounty = counties.stream().filter((c) -> c != null).findFirst();
        if (firstCounty.isPresent() && activeCaseLoadCodes.contains(firstCounty.get().getCaseloadCode())) {
            List<ResourceOrganization> resourceOrganizationsByCaseloadCode = ccmsDataService.getResourceOrganizationsByCaseloadCode(firstCounty.get().getCaseloadCode());
            return resourceOrganizationsByCaseloadCode.stream().filter((r) -> !r.getCaseloadCode().equals("SITE")).findFirst();
        }
        return Optional.empty();
    }

    @Override
    public Optional<BigInteger> getOrganizationIdByProviderId(BigInteger providerId) {
        Optional<ResourceOrganization> resourceOrganizationByProviderId = ccmsDataService.getResourceOrganizationByProviderId(
                providerId);
        return resourceOrganizationByProviderId.map(ResourceOrganization::getResourceOrgId);
    }
}
