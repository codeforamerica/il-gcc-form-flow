package org.ilgcc.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "il-gcc.pfc")
@Getter
@Setter
public class ProviderFlowConfiguration {
    public String revealAdditionalProviderScreens;
}
