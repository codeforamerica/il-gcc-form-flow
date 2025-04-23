    package org.ilgcc.app.data.ccms;

    import static org.ilgcc.app.utils.enums.CCMSRequestHeaders.CORRELATION_ID;
    import static org.ilgcc.app.utils.enums.CCMSRequestHeaders.OCP_APIM_SUBSCRIPTION_KEY;

    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import java.net.InetAddress;
    import java.net.UnknownHostException;
    import java.util.Arrays;
    import java.util.UUID;
    import lombok.extern.slf4j.Slf4j;
    import org.ilgcc.app.config.CCMSApiConfiguration;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.env.Environment;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.stereotype.Service;
    import org.springframework.web.reactive.function.client.ClientResponse;
    import org.springframework.web.reactive.function.client.WebClient;
    
    @Service
    @Slf4j
    public class CCMSApiClient {
        
        private final CCMSApiConfiguration configuration;
        private final WebClient client;
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Autowired
        public CCMSApiClient(CCMSApiConfiguration configuration, Environment env) {
            this.configuration = configuration;
            this.client = WebClient.builder()
                    .baseUrl(configuration.getBaseUrl())
                    .build();

            String[] activeProfiles = env.getActiveProfiles();
        }
        
        public JsonNode sendRequest(String endpoint, CCMSTransaction requestBody) throws JsonProcessingException {
            logContainerIp(endpoint);
            String response = client.post()
                    .uri(endpoint)
                    .headers(headers -> headers.addAll(createRequestHeaders()))
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), apiResponse -> {
                        log.error("Received an error response from CCMS when attempting to send transaction payload for submission with ID: {}. Error: {}",
                                requestBody.getSubmissionId(), getResponseDetails(apiResponse));
                        return apiResponse.createException();
                    })
                    .bodyToMono(String.class)
                    .block();
            return objectMapper.readTree(response);
        }
        
        public JsonNode sendRequest(String endpoint, CCMSTransactionLookup requestBody) throws JsonProcessingException {
            logContainerIp(endpoint);
            String response = client.post()
                    .uri(endpoint)
                    .headers(headers -> headers.addAll(createRequestHeaders()))
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), apiResponse -> {
                        log.error("Received an error response from CCMS when attempting to fetch the work item ID for Transaction with ID: {}. Error: {}",
                                requestBody.getTransactionId(), getResponseDetails(apiResponse));
                        return apiResponse.createException();
                    })
                    .bodyToMono(String.class)
                    .block();
            log.info("Received work item look up response from CCMS: {}", response);
            return objectMapper.readTree(response);
        }
        
        private HttpHeaders createRequestHeaders() {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth(configuration.getUserName(), configuration.getPassword());
            headers.set(CORRELATION_ID.getValue(), String.valueOf(UUID.randomUUID()));
            headers.set(OCP_APIM_SUBSCRIPTION_KEY.getValue(), configuration.getApiSubscriptionKey());
            return headers;
        }
        
        private void logContainerIp(String endpoint) {
            try {
                String localIp = InetAddress.getLocalHost().getHostAddress();
                log.info("Sending CCMS request to {} from container IP: {}", endpoint, localIp);
            } catch (UnknownHostException e) {
                log.warn("Could not determine container IP address", e);
            }
        }

        private String getResponseDetails(ClientResponse response) {
            StringBuilder responseDetails = new StringBuilder();
            response.bodyToMono(String.class).subscribe(body -> {
                responseDetails.append(" Status: ").append(response.statusCode());
                responseDetails.append(" Headers: ").append(response.headers().asHttpHeaders());
                responseDetails.append(" Body: ").append(body);
            });
            return responseDetails.toString();
        }
    }