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
    public static ResourceOrganization FOUR_C_TEST_DATA;
    public static County ACTIVE_FOUR_C_COUNTY;
    public static ResourceOrganization PROJECT_CHILD_TEST_DATA;
    public static County ACTIVE_PROJECT_CHILD_COUNTY;
    public static ResourceOrganization OUT_OF_SCOPE_DATA;
    public static County ACTIVE_OUT_OF_SCOPE_COUNTY;

    @Override
    public void afterPropertiesSet() {
        log.info("Starting creating fake provider data.");

        FOUR_C_TEST_DATA = new ResourceOrganization();
        FOUR_C_TEST_DATA.setResourceOrgId(new BigInteger("12345678901234"));
        FOUR_C_TEST_DATA.setName("4C: Community Coordinated Child Care");
        FOUR_C_TEST_DATA.setSda(Short.valueOf("2"));
        FOUR_C_TEST_DATA.setPhone("123456789");
        FOUR_C_TEST_DATA.setEmail("test@gmail.com");
        FOUR_C_TEST_DATA.setCaseloadCode("BB");
        resourceOrganizationRepository.save(FOUR_C_TEST_DATA);

        ACTIVE_FOUR_C_COUNTY = new County();
        ACTIVE_FOUR_C_COUNTY.setCounty("DeKalb");
        ACTIVE_FOUR_C_COUNTY.setZipCode(new BigInteger(zip_60001.getValue()));
        ACTIVE_FOUR_C_COUNTY.setCaseloadCode("BB");
        countyRepository.save(ACTIVE_FOUR_C_COUNTY);

        PROJECT_CHILD_TEST_DATA = new ResourceOrganization();
        PROJECT_CHILD_TEST_DATA.setResourceOrgId(new BigInteger("12345678901235"));
        PROJECT_CHILD_TEST_DATA.setName("Project CHILD");
        PROJECT_CHILD_TEST_DATA.setSda(Short.valueOf("15"));
        PROJECT_CHILD_TEST_DATA.setCaseloadCode("QQ");
        PROJECT_CHILD_TEST_DATA.setPhone("123456789");
        PROJECT_CHILD_TEST_DATA.setEmail("test@gmail.com");
        resourceOrganizationRepository.save(PROJECT_CHILD_TEST_DATA);

        ACTIVE_PROJECT_CHILD_COUNTY = new County();
        ACTIVE_PROJECT_CHILD_COUNTY.setCounty("Ramsey");
        ACTIVE_PROJECT_CHILD_COUNTY.setZipCode(new BigInteger(zip_62863.getValue()));
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
        ACTIVE_OUT_OF_SCOPE_COUNTY.setCounty("Chicago");
        ACTIVE_OUT_OF_SCOPE_COUNTY.setZipCode(new BigInteger(zip_60482.getValue()));
        ACTIVE_OUT_OF_SCOPE_COUNTY.setCaseloadCode("GG");
        countyRepository.save(ACTIVE_OUT_OF_SCOPE_COUNTY);


    }
}
