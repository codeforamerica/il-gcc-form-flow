package org.ilgcc.app.utils;

import jakarta.annotation.PostConstruct;
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

  @PostConstruct
  public void init() {
    activeCounties = applicationRoutingService.getActiveCountiesByCaseLoadCodes();
  }
}
