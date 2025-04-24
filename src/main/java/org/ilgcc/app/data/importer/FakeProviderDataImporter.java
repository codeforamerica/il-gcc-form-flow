package org.ilgcc.app.data.importer;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.Provider;
import org.ilgcc.app.data.ProviderRepository;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.data.ResourceOrganizationRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * A bean that runs on startup to create fake Providers, scoped to only run in specific environments
 */
@Slf4j
@Component
@Profile({"dev", "demo", "test", "staging"})
public class FakeProviderDataImporter implements InitializingBean {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ResourceOrganizationRepository resourceOrganizationRepository;

    public static Provider CURRENT_APPROVED_PROVIDER;
    public static Provider CURRENT_STATUS_R_PROVIDER;

    public static Provider CURRENT_DENIED_PROVIDER;

    public static Provider CURRENT_WITHDRAWN_PROVIDER;

    public static Provider CURRENT_STATUS_C_PROVIDER;

    public static Provider OUTDATED_APPROVED_PROVIDER;

    public static Provider OUTDATED_PENDING_PROVIDER;

    public static Provider ACTIVE_SITE_ADMINISTERED_PROVIDER;

    public static ResourceOrganization ACTIVE_SITE_ADMIN_RESOURCE_ORG;

    public static Provider ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA;

    public static ResourceOrganization ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA;

    @Override
    public void afterPropertiesSet() {
        log.info("Starting creating fake provider data.");
        Optional<Provider> approvedProvider = providerRepository.findById(new BigInteger("12345678901"));
        if (approvedProvider.isPresent()) {
            CURRENT_APPROVED_PROVIDER = approvedProvider.get();
        } else {
            CURRENT_APPROVED_PROVIDER = new Provider();
            CURRENT_APPROVED_PROVIDER.setProviderId(new BigInteger("12345678901"));
            CURRENT_APPROVED_PROVIDER.setName("Sample Provider #1");
            CURRENT_APPROVED_PROVIDER.setCity("Chicago");
            CURRENT_APPROVED_PROVIDER.setState("IL");
            CURRENT_APPROVED_PROVIDER.setZipCode("60613");
            CURRENT_APPROVED_PROVIDER.setStatus("A");
            CURRENT_APPROVED_PROVIDER.setDateOfLastApproval(OffsetDateTime.now().minusYears(1));
            providerRepository.save(CURRENT_APPROVED_PROVIDER);
        }

        if (!providerRepository.existsById(new BigInteger("12345678902"))) {
            Provider CURRENT_PENDING_PROVIDER = new Provider();
            CURRENT_PENDING_PROVIDER.setProviderId(new BigInteger("12345678902"));
            CURRENT_PENDING_PROVIDER.setName("Sample Provider #2");
            CURRENT_PENDING_PROVIDER.setCity("Chicago");
            CURRENT_PENDING_PROVIDER.setState("IL");
            CURRENT_PENDING_PROVIDER.setZipCode("60613");
            CURRENT_PENDING_PROVIDER.setStatus("P");
            CURRENT_PENDING_PROVIDER.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
            providerRepository.save(CURRENT_PENDING_PROVIDER);
        }

        Optional<Provider> rStatusProvider = providerRepository.findById(new BigInteger("12345678903"));
        if (rStatusProvider.isPresent()) {
            CURRENT_STATUS_R_PROVIDER = rStatusProvider.get();
        } else {
            CURRENT_STATUS_R_PROVIDER = new Provider();
            CURRENT_STATUS_R_PROVIDER.setProviderId(new BigInteger("12345678903"));
            CURRENT_STATUS_R_PROVIDER.setName("Sample Provider #3");
            CURRENT_STATUS_R_PROVIDER.setCity("Chicago");
            CURRENT_STATUS_R_PROVIDER.setState("IL");
            CURRENT_STATUS_R_PROVIDER.setZipCode("60613");
            CURRENT_STATUS_R_PROVIDER.setStatus("R");
            CURRENT_STATUS_R_PROVIDER.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
            providerRepository.save(CURRENT_STATUS_R_PROVIDER);
        }

        Optional<Provider> deniedProvider = providerRepository.findById(new BigInteger("12345678904"));
        if (deniedProvider.isPresent()) {
            CURRENT_DENIED_PROVIDER = deniedProvider.get();
        } else {
            CURRENT_DENIED_PROVIDER = new Provider();
            CURRENT_DENIED_PROVIDER.setProviderId(new BigInteger("12345678904"));
            CURRENT_DENIED_PROVIDER.setName("Sample Provider #4");
            CURRENT_DENIED_PROVIDER.setCity("Chicago");
            CURRENT_DENIED_PROVIDER.setState("IL");
            CURRENT_DENIED_PROVIDER.setZipCode("60613");
            CURRENT_DENIED_PROVIDER.setStatus("D");
            CURRENT_DENIED_PROVIDER.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
            providerRepository.save(CURRENT_DENIED_PROVIDER);
        }

        Optional<Provider> withdrawnProvider = providerRepository.findById(new BigInteger("12345678905"));
        if (withdrawnProvider.isPresent()) {
            CURRENT_WITHDRAWN_PROVIDER = withdrawnProvider.get();
        } else {
            CURRENT_WITHDRAWN_PROVIDER = new Provider();
            CURRENT_WITHDRAWN_PROVIDER.setProviderId(new BigInteger("12345678905"));
            CURRENT_WITHDRAWN_PROVIDER.setName("Sample Provider #5");
            CURRENT_WITHDRAWN_PROVIDER.setCity("Chicago");
            CURRENT_WITHDRAWN_PROVIDER.setState("IL");
            CURRENT_WITHDRAWN_PROVIDER.setZipCode("60613");
            CURRENT_WITHDRAWN_PROVIDER.setStatus("W");
            CURRENT_WITHDRAWN_PROVIDER.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
            providerRepository.save(CURRENT_WITHDRAWN_PROVIDER);
        }

        Optional<Provider> statusCProvider = providerRepository.findById(new BigInteger("12345678906"));
        if (statusCProvider.isPresent()) {
            CURRENT_STATUS_C_PROVIDER = statusCProvider.get();
        } else {
            CURRENT_STATUS_C_PROVIDER = new Provider();
            CURRENT_STATUS_C_PROVIDER.setProviderId(new BigInteger("12345678906"));
            CURRENT_STATUS_C_PROVIDER.setName("Sample Provider #6");
            CURRENT_STATUS_C_PROVIDER.setCity("Chicago");
            CURRENT_STATUS_C_PROVIDER.setState("IL");
            CURRENT_STATUS_C_PROVIDER.setZipCode("60613");
            CURRENT_STATUS_C_PROVIDER.setStatus("C");
            CURRENT_STATUS_C_PROVIDER.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
            providerRepository.save(CURRENT_STATUS_C_PROVIDER);
        }

        Optional<Provider> outdatedApprovedProvider = providerRepository.findById(new BigInteger("12345678907"));
        if (outdatedApprovedProvider.isPresent()) {
            OUTDATED_APPROVED_PROVIDER = outdatedApprovedProvider.get();
        } else {
            OUTDATED_APPROVED_PROVIDER = new Provider();
            OUTDATED_APPROVED_PROVIDER.setProviderId(new BigInteger("12345678907"));
            OUTDATED_APPROVED_PROVIDER.setName("Sample Provider #7");
            OUTDATED_APPROVED_PROVIDER.setCity("Chicago");
            OUTDATED_APPROVED_PROVIDER.setState("IL");
            OUTDATED_APPROVED_PROVIDER.setZipCode("60613");
            OUTDATED_APPROVED_PROVIDER.setStatus("A");
            OUTDATED_APPROVED_PROVIDER.setDateOfLastApproval(OffsetDateTime.now().minusYears(4));
            providerRepository.save(OUTDATED_APPROVED_PROVIDER);
        }

        Optional<Provider> outdatedPendingProvider = providerRepository.findById(new BigInteger("12345678908"));
        if (outdatedPendingProvider.isPresent()) {
            OUTDATED_PENDING_PROVIDER = outdatedPendingProvider.get();
        } else {
            OUTDATED_PENDING_PROVIDER = new Provider();
            OUTDATED_PENDING_PROVIDER.setProviderId(new BigInteger("12345678908"));
            OUTDATED_PENDING_PROVIDER.setName("Sample Provider #8");
            OUTDATED_PENDING_PROVIDER.setCity("Chicago");
            OUTDATED_PENDING_PROVIDER.setState("IL");
            OUTDATED_PENDING_PROVIDER.setZipCode("60613");
            OUTDATED_PENDING_PROVIDER.setStatus("P");
            OUTDATED_PENDING_PROVIDER.setDateOfLastApproval(OffsetDateTime.now().minusYears(5));
            providerRepository.save(OUTDATED_PENDING_PROVIDER);
        }

        Optional<ResourceOrganization> activeResourceOrganization = resourceOrganizationRepository.findById(
                new BigInteger("10101"));

        if (activeResourceOrganization.isPresent()) {
            ACTIVE_SITE_ADMIN_RESOURCE_ORG = activeResourceOrganization.get();
        } else {
            ACTIVE_SITE_ADMIN_RESOURCE_ORG = new ResourceOrganization();
            ACTIVE_SITE_ADMIN_RESOURCE_ORG.setResourceOrgId(new BigInteger("10101"));
            ACTIVE_SITE_ADMIN_RESOURCE_ORG.setName("Sample Site Admin Resource Organization");
            ACTIVE_SITE_ADMIN_RESOURCE_ORG.setPhone("(999) 123-1234");
            ACTIVE_SITE_ADMIN_RESOURCE_ORG.setCaseloadCode("SITE");
            ACTIVE_SITE_ADMIN_RESOURCE_ORG.setCity("Chicago");
            ACTIVE_SITE_ADMIN_RESOURCE_ORG.setSda((short) 2);
            resourceOrganizationRepository.save(ACTIVE_SITE_ADMIN_RESOURCE_ORG);
        }

        Optional<Provider> activeSiteAdministeredProvider = providerRepository.findById(new BigInteger("12345678909"));

        if (activeSiteAdministeredProvider.isPresent()) {
            ACTIVE_SITE_ADMINISTERED_PROVIDER = activeSiteAdministeredProvider.get();
        } else {
            ACTIVE_SITE_ADMINISTERED_PROVIDER = new Provider();
            ACTIVE_SITE_ADMINISTERED_PROVIDER.setProviderId(new BigInteger("12345678909"));
            ACTIVE_SITE_ADMINISTERED_PROVIDER.setName("Sample Provider #9");
            ACTIVE_SITE_ADMINISTERED_PROVIDER.setCity("Chicago");
            ACTIVE_SITE_ADMINISTERED_PROVIDER.setState("IL");
            ACTIVE_SITE_ADMINISTERED_PROVIDER.setZipCode("60613");
            ACTIVE_SITE_ADMINISTERED_PROVIDER.setStatus("A");
            ACTIVE_SITE_ADMINISTERED_PROVIDER.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
        }

        ACTIVE_SITE_ADMINISTERED_PROVIDER.setResourceOrganization(ACTIVE_SITE_ADMIN_RESOURCE_ORG);
        providerRepository.save(ACTIVE_SITE_ADMINISTERED_PROVIDER);

        Optional<ResourceOrganization> resourceOrganizationOutsideSDA = resourceOrganizationRepository.findById(
                new BigInteger("10102"));

        if (resourceOrganizationOutsideSDA.isPresent()) {
            ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA = activeResourceOrganization.get();
        } else {
            ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA = new ResourceOrganization();
            ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA.setResourceOrgId(new BigInteger("10102"));
            ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA.setName("Sample Site Admin Resource Organization Outside SDA");
            ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA.setPhone("(999) 123-1234");
            ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA.setCaseloadCode("SITE");
            ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA.setCity("Chicago");
            ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA.setSda((short) 10);
            resourceOrganizationRepository.save(ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA);
        }

        Optional<Provider> activeSiteAdministeredProviderOutOfSDA = providerRepository.findById(new BigInteger("12345678910"));

        if (activeSiteAdministeredProviderOutOfSDA.isPresent()) {
            ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA = activeSiteAdministeredProviderOutOfSDA.get();
        } else {
            ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA = new Provider();
            ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA.setProviderId(new BigInteger("12345678910"));
            ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA.setName("Site Administered Provider");
            ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA.setCity("Chicago");
            ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA.setState("IL");
            ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA.setZipCode("60613");
            ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA.setStatus("A");
            ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
        }

        ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA.setResourceOrganization(ACTIVE_SITE_ADMIN_RESOURCE_ORG_OUTSIDE_ACTIVE_SDA);
        providerRepository.save(ACTIVE_SITE_ADMINISTERED_PROVIDER_OUTSIDE_SDA);

        log.info("Completed creating fake provider data.");
    }
}
