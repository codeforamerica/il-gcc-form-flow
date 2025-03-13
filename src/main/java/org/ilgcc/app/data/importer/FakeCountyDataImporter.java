package org.ilgcc.app.data.importer;

import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.County;
import org.ilgcc.app.data.CountyRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * A bean that runs on startup to create fake Providers, scoped to only run in specific environments
 */
@Slf4j
@Component
@Profile({"dev", "demo"})
public class FakeCountyDataImporter implements InitializingBean {

    @Autowired
    private CountyRepository countyRepository;

    @Override
    public void afterPropertiesSet() {
        log.info("Starting creating fake county/zipcode data.");


        if (!countyRepository.existsById("60002")) {
            County SDA2_COUNTY = new County();
            SDA2_COUNTY.setCounty("DEKALB(SDA2)");
            SDA2_COUNTY.setZipCode(new BigInteger("60002"));
            SDA2_COUNTY.setCaseloadCode("BB");
            countyRepository.save(SDA2_COUNTY);
        }

        if (!countyRepository.existsById("60015")) {
            County SDA2_COUNTY = new County();
            SDA2_COUNTY.setCounty("FAYETTE(SDA15)");
            SDA2_COUNTY.setZipCode(new BigInteger("60015"));
            SDA2_COUNTY.setCaseloadCode("QQ");
            countyRepository.save(SDA2_COUNTY);
        }

        if (!countyRepository.existsById("60006")) {
            County SDA2_COUNTY = new County();
            SDA2_COUNTY.setCounty("COOK(SDA6)");
            SDA2_COUNTY.setZipCode(new BigInteger("60006"));
            SDA2_COUNTY.setCaseloadCode("GG");
            countyRepository.save(SDA2_COUNTY);
        }


        log.info("Completed creating fake county/zipcode data.");
    }
}
