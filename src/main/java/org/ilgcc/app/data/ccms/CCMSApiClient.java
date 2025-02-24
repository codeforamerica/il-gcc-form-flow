package org.ilgcc.app.data.ccms;

import static org.ilgcc.app.utils.enums.CCMSRequestHeaders.OCP_APIM_SUBSCRIPTION_KEY;

import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.config.CCMSApiConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class CCMSApiClient {
    
    private final CCMSApiConfiguration configuration;
    private final WebClient client;

    @Autowired
    public CCMSApiClient(CCMSApiConfiguration configuration) {
        this.configuration = configuration;
        this.client = WebClient.builder()
                .baseUrl(configuration.getBaseUrl())
                .build();
    }
    
    public String sendRequest(String endpoint, CCMSTransaction requestBody) {
        return client.post()
                .uri(endpoint)
                .headers(headers -> headers.addAll(createRequestHeaders()))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    
    public String sendRequest(String endpoint, CCMSTransactionLookup requestBody) {
        return client.post()
                .uri(endpoint)
                .headers(headers -> headers.addAll(createRequestHeaders()))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    
    private HttpHeaders createRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(configuration.getUserName(), configuration.getPassword());
        headers.set(OCP_APIM_SUBSCRIPTION_KEY.getValue(), configuration.getApiSubscriptionKey());
        return headers;
    }
}
