package org.ilgcc.app.data.importer;


import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.County;
import org.ilgcc.app.data.CountyRepository;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.data.ResourceOrganizationRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile({"dev", "demo", "test"})
public class FakeResourceOrganizationAndCountyData implements InitializingBean {

    @Autowired
    private ResourceOrganizationRepository resourceOrganizationRepository;
    @Autowired
    private CountyRepository countyRepository;
    public static ResourceOrganization FOUR_C_TEST_DATA;
    public static County ACTIVE_FOUR_C_COUNTY;
    public static County ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60051;
    public static County ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60097;
    public static ResourceOrganization PROJECT_CHILD_TEST_DATA;
    public static County ACTIVE_PROJECT_CHILD_COUNTY;
    public static ResourceOrganization OUT_OF_SCOPE_DATA;
    public static County ACTIVE_OUT_OF_SCOPE_COUNTY;

    @Override
    public void afterPropertiesSet() {
        log.info("Starting creating fake resource org and zipcode/county data.");

        FOUR_C_TEST_DATA = new ResourceOrganization();
        FOUR_C_TEST_DATA.setResourceOrgId(new BigInteger("12345678901234"));
        FOUR_C_TEST_DATA.setName("4C: Community Coordinated Child Care");
        FOUR_C_TEST_DATA.setSda(Short.valueOf("2"));
        FOUR_C_TEST_DATA.setPhone("123456789");
        FOUR_C_TEST_DATA.setEmail("test@gmail.com");
        FOUR_C_TEST_DATA.setCaseloadCode("BB");
        resourceOrganizationRepository.save(FOUR_C_TEST_DATA);

        ACTIVE_FOUR_C_COUNTY = new County();
        ACTIVE_FOUR_C_COUNTY.setCounty("DEKALB");
        ACTIVE_FOUR_C_COUNTY.setZipCode(new BigInteger("60002"));
        ACTIVE_FOUR_C_COUNTY.setCaseloadCode("BB");
        countyRepository.save(ACTIVE_FOUR_C_COUNTY);

        ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60051 = new County();
        ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60051.setCounty("MCHENRY");
        ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60051.setZipCode(new BigInteger("60051"));
        ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60051.setCaseloadCode("BB");
        countyRepository.save(ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60051);

        ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60097 = new County();
        ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60097.setCounty("MCHENRY");
        ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60097.setZipCode(new BigInteger("60097"));
        ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60097.setCaseloadCode("BB");
        countyRepository.save(ACTIVE_FOUR_C_COUNTY_MCHENRY_ZIPCODE_60097);

        PROJECT_CHILD_TEST_DATA = new ResourceOrganization();
        PROJECT_CHILD_TEST_DATA.setResourceOrgId(new BigInteger("12345678901235"));
        PROJECT_CHILD_TEST_DATA.setName("Project CHILD");
        PROJECT_CHILD_TEST_DATA.setSda(Short.valueOf("15"));
        PROJECT_CHILD_TEST_DATA.setCaseloadCode("QQ");
        PROJECT_CHILD_TEST_DATA.setPhone("123456789");
        PROJECT_CHILD_TEST_DATA.setEmail("test@gmail.com");
        resourceOrganizationRepository.save(PROJECT_CHILD_TEST_DATA);

        ACTIVE_PROJECT_CHILD_COUNTY = new County();
        ACTIVE_PROJECT_CHILD_COUNTY.setCounty("FAYETTE");
        ACTIVE_PROJECT_CHILD_COUNTY.setZipCode(new BigInteger("60015"));
        ACTIVE_PROJECT_CHILD_COUNTY.setCaseloadCode("QQ");
        countyRepository.save(ACTIVE_PROJECT_CHILD_COUNTY);

        OUT_OF_SCOPE_DATA = new ResourceOrganization();
        OUT_OF_SCOPE_DATA.setResourceOrgId(new BigInteger("12345678901236"));
        OUT_OF_SCOPE_DATA.setName("Illinois Action for Children");
        OUT_OF_SCOPE_DATA.setSda(Short.valueOf("6"));
        OUT_OF_SCOPE_DATA.setCaseloadCode("GG");
        OUT_OF_SCOPE_DATA.setPhone("123456789");
        OUT_OF_SCOPE_DATA.setEmail("test@gmail.com");
        resourceOrganizationRepository.save(OUT_OF_SCOPE_DATA);

        ACTIVE_OUT_OF_SCOPE_COUNTY = new County();
        ACTIVE_OUT_OF_SCOPE_COUNTY.setCounty("COOK");
        ACTIVE_OUT_OF_SCOPE_COUNTY.setZipCode(new BigInteger("60006"));
        ACTIVE_OUT_OF_SCOPE_COUNTY.setCaseloadCode("GG");
        countyRepository.save(ACTIVE_OUT_OF_SCOPE_COUNTY);


    }
}
