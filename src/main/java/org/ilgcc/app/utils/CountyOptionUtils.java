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
import java.util.Optional;
import lombok.Getter;
import org.ilgcc.app.data.County;
import org.ilgcc.app.submission.router.ApplicationRoutingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CountyOptionUtils {

  @Autowired
  private ApplicationRoutingServiceImpl applicationRoutingService;

  private static List<County> activeCounties;

  @PostConstruct
  public void init() {
    // Load data from the database on startup
    Optional<List<County>> optionalActiiveCounties = applicationRoutingService.getActiveCountiesByCaseLoadCodes();
    activeCounties = optionalActiiveCounties.orElseGet(ArrayList::new);
  }

  public static List<County> getActiveCounties() {
    return activeCounties;
  }
}
