package org.ilgcc.app.data;

import formflow.library.data.Submission;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
@DataJpaTest
@AutoConfigureEmbeddedDatabase(type = DatabaseType.POSTGRES, provider = DatabaseProvider.ZONKY)
@EnableJpaRepositories(basePackageClasses = {TransmissionRepository.class, ProviderRepository.class, County.class, ResourceOrganization.class})
@EntityScan(basePackageClasses = {Transmission.class, Submission.class, Provider.class, County.class, ResourceOrganization.class})
@ActiveProfiles("test")
public class EmbeddedPostgresIntegrationTest {

    private static EmbeddedPostgres embeddedPostgres;

    @Autowired
    private ProviderRepository repository;

    @Autowired
    private CountyRepository countyRepository;

    @Autowired
    private ResourceOrganizationRepository resourceOrganizationRepository ;

    @Test
    public void testFindProviderById() {
        BigInteger id = BigInteger.valueOf(43545l);
        Provider provider = new Provider();
        provider.setName("New Provider");
        provider.setProviderId(id);
        Provider savedPerson = repository.save(provider);
        Assertions.assertNotNull(savedPerson.getProviderId());
        Assertions.assertEquals(savedPerson.getProviderId(), provider.getProviderId());
    }

    @Test
    public void testFindCountyById() {
        County county = new County();
        county.setCounty("Test County");
        county.setCity("Test City");
        county.setZipCode("12345");
        County savedCounty = countyRepository.save(county);

        Optional<County> byId = countyRepository.findById("12345");
        County countyById = byId.get();
        Assertions.assertNotNull(countyById);
        Assertions.assertEquals(savedCounty, countyById);
    }

    @Test
    public void testResourceOrganizationById() {
        BigInteger id = BigInteger.valueOf(43545l);
        String caseloadCode = "TestCode";
        ResourceOrganization organization = new ResourceOrganization();
        organization.setSda(Short.valueOf("3434"));
        organization.setResourceOrgId(id);
        organization.setCaseloadCode(caseloadCode);
        organization.setName("test name");
        ResourceOrganization savedOrganization = resourceOrganizationRepository.save(organization);

        List<ResourceOrganization> byIds = resourceOrganizationRepository.findByCaseloadCode(caseloadCode);
        Assertions.assertNotNull(byIds);
        Assertions.assertEquals(savedOrganization, byIds.get(0));
    }

    @Test
    public void testResourceOrganizationByProviderId() {
        BigInteger id = BigInteger.valueOf(34334l);
        String caseloadCode = "TestCode";
        ResourceOrganization organization = new ResourceOrganization();
        organization.setSda(Short.valueOf("3434"));
        organization.setResourceOrgId(id);
        organization.setCaseloadCode(caseloadCode);
        organization.setName("test name");
        ResourceOrganization savedOrganization = resourceOrganizationRepository.save(organization);

        BigInteger providerId = BigInteger.valueOf(1213l);
        Provider provider = new Provider();
        provider.setName("New Provider");
        provider.setProviderId(providerId);
        provider.setResourceOrganization(savedOrganization);
        repository.save(provider);

        Optional<ResourceOrganization> byId = resourceOrganizationRepository.findByProvidersProviderId(providerId);
        ResourceOrganization resourceOrganization = byId.get();
        Assertions.assertNotNull(resourceOrganization);
        Assertions.assertEquals(savedOrganization, resourceOrganization);
    }
}