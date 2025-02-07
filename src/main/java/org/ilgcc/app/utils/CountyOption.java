package org.ilgcc.app.utils;

import static org.ilgcc.app.utils.ZipcodeOption.zip_60050;
import static org.ilgcc.app.utils.ZipcodeOption.zip_60115;
import static org.ilgcc.app.utils.ZipcodeOption.zip_60530;
import static org.ilgcc.app.utils.ZipcodeOption.zip_61015;
import static org.ilgcc.app.utils.ZipcodeOption.zip_61053;
import static org.ilgcc.app.utils.ZipcodeOption.zip_61071;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62011;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62401;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62410;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62413;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62421;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62432;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62434;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62439;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62446;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62476;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62807;
import static org.ilgcc.app.utils.ZipcodeOption.zip_62814;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum CountyOption implements InputOption {

    CARROLL("Carroll", zip_61053),
    DEKALB("DeKalb", zip_60115),
    LEE("Lee", zip_60530),
    MCHENRY("McHenry", zip_60050),
    OGLE("Ogle", zip_61015),
    WHITESIDE("Whiteside", zip_61071),
    MARION("Marion", zip_62807),
    JEFFERSON("Jefferson", zip_62814),
    EFFINGHAM("Effingham", zip_62401),
    FAYETTE("Fayette", zip_62011),
    CRAWFORD("Crawford", zip_62413),
    WAYNE("Wayne", zip_62446),
    RICHLAND("Richland", zip_62421),
    LAWRENCE("Lawrence", zip_62439),
    CLAY("Clay", zip_62434),
    WABASH("Wabash", zip_62410),
    JASPER("Jasper", zip_62432),
    EDWARDS("Edwards", zip_62476);

    private final String value;
    private final ZipcodeOption zipcodeOption;

    static private final Map<String, ZipcodeOption> COUNTY_MAP = new HashMap<>();

    static {
        for (CountyOption county : CountyOption.values()) {
            COUNTY_MAP.put(county.value, county.zipcodeOption);
        }
    }

    CountyOption(String value, ZipcodeOption zipcodeOption) {
        this.value = value;
        this.zipcodeOption = zipcodeOption;
    }

    @Override
    public String getLabel() {
        return this.value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getHelpText() {
        return null;
    }

    public static Optional<ZipcodeOption> getZipCodeFromCountyName(String countyName) {
        boolean hasCounty = COUNTY_MAP.containsKey(countyName);
        return hasCounty ? Optional.of(COUNTY_MAP.get(countyName)) : Optional.empty();
    }
}
