package org.ilgcc.app.data;

import static org.ilgcc.app.utils.ZipcodeOption.zip_60001;
import static org.ilgcc.app.utils.ZipcodeOption.zip_60482;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62863;

import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile({"test"})
public class FakeResourceOrganization implements InitializingBean {
    @Autowired
    private ResourceOrganizationRepository resourceOrganizationRepository;
    @Autowired
    private CountyRepository countyRepository;

    @Override
    public void afterPropertiesSet() {
        log.info("Starting creating fake provider data.");

        if (!resourceOrganizationRepository.existsById(new BigInteger("56522729391679"))) {
            ResourceOrganization org = new ResourceOrganization();
            org.setResourceOrgId(new BigInteger("56522729391679"));
            org.setName("4C: Community Coordinated Child Care");
            org.setSda(Short.valueOf("2"));
            org.setPhone("123456789");
            org.setEmail("test@gmail.com");
            org.setCaseloadCode("BB");
            resourceOrganizationRepository.save(org);
        }

        if (!resourceOrganizationRepository.existsById(new BigInteger("56522729391679"))) {
            ResourceOrganization org = new ResourceOrganization();
            org.setResourceOrgId(new BigInteger("56522729391679"));
            org.setName("Project CHILD");
            org.setSda(Short.valueOf("15"));
            org.setCaseloadCode("QQ");
            org.setPhone("123456789");
            org.setEmail("test@gmail.com");
            resourceOrganizationRepository.save(org);
        }

        if (!resourceOrganizationRepository.existsById(new BigInteger("47522729391670"))) {
            ResourceOrganization org = new ResourceOrganization();
            org.setResourceOrgId(new BigInteger("47522729391670"));
            org.setName("Illinois Action for Children");
            org.setSda(Short.valueOf("6"));
            org.setCaseloadCode("GG");
            org.setPhone("123456789");
            org.setEmail("test@gmail.com");
            resourceOrganizationRepository.save(org);
        }

        if (!countyRepository.existsById(zip_60482.getValue())) {
            County county = new County();
            county.setCounty("DeKalb");
            county.setZipCode(new BigInteger(zip_60482.getValue()));
            county.setCaseloadCode("GG");
            countyRepository.save(county);
        }

        if (!countyRepository.existsById(zip_60001.getValue())) {
            County county = new County();
            county.setCounty("DeKalb");
            county.setZipCode(new BigInteger(zip_60001.getValue()));
            county.setCaseloadCode("BB");
            countyRepository.save(county);
        }

        if (!countyRepository.existsById(zip_62863.getValue())) {
            County county = new County();
            county.setCounty("Ramsey");
            county.setZipCode(new BigInteger(zip_62863.getValue()));
            county.setCaseloadCode("QQ");
            countyRepository.save(county);
        }

    }
}
