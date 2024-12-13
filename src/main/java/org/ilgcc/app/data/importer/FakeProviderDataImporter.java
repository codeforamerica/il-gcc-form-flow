package org.ilgcc.app.data.importer;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.Provider;
import org.ilgcc.app.data.ProviderRepository;
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

    @Override
    public void afterPropertiesSet() {
        log.info("Starting creating fake provider data.");

        // Valid date, status A
        Provider provider = new Provider();
        provider.setProviderId(new BigInteger("12345678901"));
        provider.setName("Sample Provider #1");
        provider.setCity("Chicago");
        provider.setState("IL");
        provider.setZipCode("60613");
        provider.setStatus("A");
        provider.setDateOfLastApproval(OffsetDateTime.now().minusYears(1));
        providerRepository.save(provider);

        // Valid date, status P
        provider = new Provider();
        provider.setProviderId(new BigInteger("12345678902"));
        provider.setName("Sample Provider #2");
        provider.setCity("Chicago");
        provider.setState("IL");
        provider.setZipCode("60613");
        provider.setStatus("P");
        provider.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
        providerRepository.save(provider);

        // Valid date, status R
        provider = new Provider();
        provider.setProviderId(new BigInteger("12345678903"));
        provider.setName("Sample Provider #3");
        provider.setCity("Chicago");
        provider.setState("IL");
        provider.setZipCode("60613");
        provider.setStatus("R");
        provider.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
        providerRepository.save(provider);

        // Valid date, status D
        provider = new Provider();
        provider.setProviderId(new BigInteger("12345678904"));
        provider.setName("Sample Provider #4");
        provider.setCity("Chicago");
        provider.setState("IL");
        provider.setZipCode("60613");
        provider.setStatus("D");
        provider.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
        providerRepository.save(provider);

        // Valid date, status W
        provider = new Provider();
        provider.setProviderId(new BigInteger("12345678905"));
        provider.setName("Sample Provider #5");
        provider.setCity("Chicago");
        provider.setState("IL");
        provider.setZipCode("60613");
        provider.setStatus("W");
        provider.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
        providerRepository.save(provider);

        // Valid date, status C
        provider = new Provider();
        provider.setProviderId(new BigInteger("12345678906"));
        provider.setName("Sample Provider #6");
        provider.setCity("Chicago");
        provider.setState("IL");
        provider.setZipCode("60613");
        provider.setStatus("C");
        provider.setDateOfLastApproval(OffsetDateTime.now().minusYears(2));
        providerRepository.save(provider);

        // Invalid date, status A
        provider = new Provider();
        provider.setProviderId(new BigInteger("12345678907"));
        provider.setName("Sample Provider #7");
        provider.setCity("Chicago");
        provider.setState("IL");
        provider.setZipCode("60613");
        provider.setStatus("A");
        provider.setDateOfLastApproval(OffsetDateTime.now().minusYears(4));
        providerRepository.save(provider);

        // Invalid date, status P
        provider = new Provider();
        provider.setProviderId(new BigInteger("12345678908"));
        provider.setName("Sample Provider #8");
        provider.setCity("Chicago");
        provider.setState("IL");
        provider.setZipCode("60613");
        provider.setStatus("P");
        provider.setDateOfLastApproval(OffsetDateTime.now().minusYears(5));
        providerRepository.save(provider);

        log.info("Completed creating fake provider data.");
    }
}
