package org.ilgcc.app.utils;

import static org.ilgcc.app.utils.ZipcodeOption.zip_60050;
import static org.ilgcc.app.utils.ZipcodeOption.zip_60115;
import static org.ilgcc.app.utils.ZipcodeOption.zip_60530;
import static org.ilgcc.app.utils.ZipcodeOption.zip_61015;
import static org.ilgcc.app.utils.ZipcodeOption.zip_61053;
import static org.ilgcc.app.utils.ZipcodeOption.zip_61243;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum CountyOption implements InputOption {

    CARROLL("Carroll", zip_61053),
    DEKALB("DeKalb", zip_60115),
    LEE("Lee", zip_60530),
    MCHENRY("McHenry", zip_60050),
    OGLE("Ogle", zip_61015),
    WHITESIDE("Whiteside", zip_61243);

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
