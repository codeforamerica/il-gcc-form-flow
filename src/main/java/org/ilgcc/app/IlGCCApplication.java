package org.ilgcc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"org.ilgcc.app", "formflow.library", "org.ilgcc.jobs"})
@EnableConfigurationProperties
@EnableRetry
@EnableAsync
public class IlGCCApplication {

  public static void main(String[] args) {
    SpringApplication.run(IlGCCApplication.class, args);
  }

}
