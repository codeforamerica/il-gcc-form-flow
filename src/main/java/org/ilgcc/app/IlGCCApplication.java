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
    System.out.println("SPRING_PROFILES_ACTIVE: " + System.getenv("SPRING_PROFILES_ACTIVE"));
    System.out.println("DATABASE_HOST: " + System.getenv("DATABASE_HOST"));
    System.out.println("DATABASE_USER: " + System.getenv("DATABASE_USER"));
    System.out.println("DATABASE_PASSWORD: " + System.getenv("DATABASE_PASSWORD"));
//    try {
      SpringApplication.run(IlGCCApplication.class, args);
//    } catch (Throwable t) {
//      t.printStackTrace();
//      System.err.println("FATAL ERROR: " + t.getMessage());
//      System.exit(1);
//    }
}
}
