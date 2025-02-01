package org.ilgcc.app.utils;

import static org.ilgcc.app.utils.CountyOption.CARROLL;
import static org.ilgcc.app.utils.CountyOption.CLAY;
import static org.ilgcc.app.utils.CountyOption.CRAWFORD;
import static org.ilgcc.app.utils.CountyOption.DEKALB;
import static org.ilgcc.app.utils.CountyOption.EDWARDS;
import static org.ilgcc.app.utils.CountyOption.EFFINGHAM;
import static org.ilgcc.app.utils.CountyOption.FAYETTE;
import static org.ilgcc.app.utils.CountyOption.JASPER;
import static org.ilgcc.app.utils.CountyOption.JEFFERSON;
import static org.ilgcc.app.utils.CountyOption.LAWRENCE;
import static org.ilgcc.app.utils.CountyOption.LEE;
import static org.ilgcc.app.utils.CountyOption.MARION;
import static org.ilgcc.app.utils.CountyOption.MCHENRY;
import static org.ilgcc.app.utils.CountyOption.OGLE;
import static org.ilgcc.app.utils.CountyOption.RICHLAND;
import static org.ilgcc.app.utils.CountyOption.WABASH;
import static org.ilgcc.app.utils.CountyOption.WAYNE;
import static org.ilgcc.app.utils.CountyOption.WHITESIDE;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CountyOptionUtils {
    @Value("${il-gcc.enable-sda15-providers}")
    boolean enableSDA15Providers;

    private static List<CountyOption> countyOptions;

    @PostConstruct
    public void init() {
        countyOptions = new ArrayList<>();
        countyOptions.add(CARROLL);
        countyOptions.add(DEKALB);
        countyOptions.add(LEE);
        countyOptions.add(MCHENRY);
        countyOptions.add(OGLE);
        countyOptions.add(WHITESIDE);
        if (enableSDA15Providers) {
            countyOptions.add(MARION);
            countyOptions.add(JEFFERSON);
            countyOptions.add(EFFINGHAM);
            countyOptions.add(FAYETTE);
            countyOptions.add(CRAWFORD);
            countyOptions.add(WAYNE);
            countyOptions.add(RICHLAND);
            countyOptions.add(LAWRENCE);
            countyOptions.add(CLAY);
            countyOptions.add(WABASH);
            countyOptions.add(JASPER);
            countyOptions.add(EDWARDS);
        }
    }

    public static List<CountyOption> getActiveCountyOptions() {
        return countyOptions;
    }
}
