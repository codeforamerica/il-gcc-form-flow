package org.ilgcc.app.data.ccms;

import static org.ilgcc.app.utils.enums.CCMSRequestHeaders.CORRELATION_ID;
import static org.ilgcc.app.utils.enums.CCMSRequestHeaders.OCP_APIM_SUBSCRIPTION_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.config.CCMSApiConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CCMSApiClient {

    @Getter
    private final CCMSApiConfiguration configuration;
    private final WebClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public CCMSApiClient(CCMSApiConfiguration configuration) {
        this.configuration = configuration;
        this.client = WebClient.builder()
                .baseUrl(configuration.getBaseUrl())
                .build();
    }

    public JsonNode sendRequest(String endpoint, CCMSTransaction requestBody) throws JsonProcessingException {
        String response = client.post()
                .uri(endpoint)
                .headers(h -> h.addAll(createRequestHeaders()))
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), resp ->
                        resp.bodyToMono(String.class).flatMap(body -> {
                            String errorMessage = String.format(
                                    "Received an error response from CCMS when sending submission with ID: %s. Status: %s, Body: %s",
                                    requestBody.getSubmissionId(),
                                    resp.statusCode(),
                                    body
                            );

                            log.warn(errorMessage);

                            return Mono.error(new CCMSException(
                                    errorMessage,
                                    resp.statusCode(),
                                    body
                            ));
                        })
                )
                .bodyToMono(String.class)
                .doOnError(e -> {
                    if (!(e instanceof CCMSException)) {
                        log.error("Unexpected error occurred during API call for submission ID: {}",
                                requestBody.getSubmissionId(), e);
                    }
                })
                .block();

        return objectMapper.readTree(response);
    }

    public JsonNode fetchRequest(String endpoint, CCMSTransactionLookup requestBody) throws JsonProcessingException {
        String response = client.post()
                .uri(endpoint)
                .headers(headers -> headers.addAll(createRequestHeaders()))
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), apiResponse -> {
                    log.error(
                            "Received an error response from CCMS when attempting to fetch the work item ID for Transaction with ID: {}. Error: {}",
                            requestBody.getTransactionId(), apiResponse);
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
        headers.set(CORRELATION_ID.getValue(), String.valueOf(UUID.randomUUID()));
        headers.set(OCP_APIM_SUBSCRIPTION_KEY.getValue(), configuration.getApiSubscriptionKey());
        return headers;
    }

    private HttpHeaders createFetchHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(OCP_APIM_SUBSCRIPTION_KEY.getValue(), configuration.getApiSubscriptionKey());
        return headers;
    }
}