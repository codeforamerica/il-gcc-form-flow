package org.ilgcc.app.utils;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.ilgcc.app.data.County;
import org.ilgcc.app.submission.router.ApplicationRoutingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CountyUtils {

    @Autowired
    private ApplicationRoutingServiceImpl applicationRoutingService;

    @Getter
    private static List<County> activeCounties;

    public static List<String> activeCountiesProperCapitalized = new ArrayList<>();

    @PostConstruct
    public void init() {
        activeCounties = applicationRoutingService.getActiveCountiesByCaseLoadCodes();
        for (County countyName : activeCounties) {
            activeCountiesProperCapitalized.add(capitalizeCounty(countyName.getCounty()));
        }
    }

    private static String capitalizeCounty(String countyName) {
        String lowercaseCounty = countyName.toLowerCase();
        if (lowercaseCounty.startsWith("mch") || lowercaseCounty.startsWith("dek")) {
            return lowercaseCounty.substring(0, 1).toUpperCase() + lowercaseCounty.substring(1, 2) + lowercaseCounty.substring(2,
                    3).toUpperCase() + lowercaseCounty.substring(3);
        }
        return lowercaseCounty.substring(0, 1).toUpperCase() + lowercaseCounty.substring(1);
    }
}
