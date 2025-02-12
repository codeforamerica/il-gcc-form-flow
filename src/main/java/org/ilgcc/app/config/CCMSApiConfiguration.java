package org.ilgcc.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "il-gcc.ccms-api")
@Getter
@Setter
public class CCMSApiConfiguration {
    String apiSubscriptionKey;
    String userName;
    String password;
    String baseUrl;
}
