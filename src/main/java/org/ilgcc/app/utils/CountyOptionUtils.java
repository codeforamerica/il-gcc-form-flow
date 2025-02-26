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
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CountyOptionUtils {

    private static List<CountyOption> countyOptions;

    @PostConstruct
    public void init() {
        List<CountyOption> unsortedCountyOptions = new ArrayList<>();
        unsortedCountyOptions.add(CARROLL);
        unsortedCountyOptions.add(DEKALB);
        unsortedCountyOptions.add(LEE);
        unsortedCountyOptions.add(MCHENRY);
        unsortedCountyOptions.add(OGLE);
        unsortedCountyOptions.add(WHITESIDE);
        unsortedCountyOptions.add(MARION);
        unsortedCountyOptions.add(JEFFERSON);
        unsortedCountyOptions.add(EFFINGHAM);
        unsortedCountyOptions.add(FAYETTE);
        unsortedCountyOptions.add(CRAWFORD);
        unsortedCountyOptions.add(WAYNE);
        unsortedCountyOptions.add(RICHLAND);
        unsortedCountyOptions.add(LAWRENCE);
        unsortedCountyOptions.add(CLAY);
        unsortedCountyOptions.add(WABASH);
        unsortedCountyOptions.add(JASPER);
        unsortedCountyOptions.add(EDWARDS);
        countyOptions = unsortedCountyOptions.stream().sorted(Comparator.comparing(CountyOption::getLabel)).toList();
    }

    public static List<CountyOption> getActiveCountyOptions() {
        return countyOptions;
    }
}
