package org.ilgcc.app.utils;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

    private static List<String> countyCapitalizationExceptions = List.of("mch", "dek", "dew", "dup", "mcl");

    @PostConstruct
    public void init() {
        activeCounties = applicationRoutingService.getActiveCountiesByCaseLoadCodes();
        for (County countyName : activeCounties) {
            activeCountiesProperCapitalized.add(capitalizeCounty(countyName.getCounty()));
        }
    }

    private static String capitalizeCounty(String countyName) {
        List<String> countryNameListed = Arrays.stream(countyName.toLowerCase().split(" ")).toList();
        List<String> updatedCountyNameListed = new ArrayList<>();
        for(String name : countryNameListed){
            if (name.length() > 3 && countyCapitalizationExceptions.contains(name.substring(0, 3))) {
                updatedCountyNameListed.add(name.substring(0, 1).toUpperCase() + name.substring(1, 2) + name.substring(2,
                        3).toUpperCase() + name.substring(3));
            } else {
                updatedCountyNameListed.add(name.substring(0, 1).toUpperCase() + name.substring(1));
            }
        }
        return String.join(" ", updatedCountyNameListed);
    }
}
