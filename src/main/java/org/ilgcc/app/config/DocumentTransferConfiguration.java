package org.ilgcc.app.config;

import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "il-gcc.dts")
@Getter
@Setter
public class DocumentTransferConfiguration {
    public String url;
    public String consumerId;
    public String authToken;
    public Long presignedUrlDuration;
    public String processingOrg;
    public String waitForProviderResponse;
}
