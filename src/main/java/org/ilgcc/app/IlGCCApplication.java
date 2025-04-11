package org.ilgcc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"org.ilgcc.app", "formflow.library", "org.ilgcc.jobs"})
@EnableConfigurationProperties
@EnableRetry
@EnableAsync
public class IlGCCApplication {

    public static void main(String[] args) {
      ConfigurableApplicationContext context = SpringApplication.run(IlGCCApplication.class, args);
      System.out.println("SPRING_PROFILES_ACTIVE: " + System.getenv("SPRING_PROFILES_ACTIVE"));
        Environment env = context.getEnvironment();
        System.out.println("ACTIVE PROFILE IS: " + String.join(", ", env.getActiveProfiles()));
    }
}
