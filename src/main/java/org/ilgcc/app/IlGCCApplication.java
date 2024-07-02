package org.ilgcc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"org.ilgcc.app", "formflow.library", "org.ilgcc.jobs"})
@EnableConfigurationProperties
public class IlGCCApplication {

  public static void main(String[] args) {
    SpringApplication.run(IlGCCApplication.class, args);
  }

}
