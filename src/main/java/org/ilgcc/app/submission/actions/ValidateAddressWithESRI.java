package org.ilgcc.app.submission.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.inputs.FieldNameMarkers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@Component
public class ValidateAddressWithESRI implements Action {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    private static final String streetAddress1Key = "StreetAddress1";
    private static final String cityKey = "City";
    private static final String stateKey = "State";
    private static final String zipCodeKey = "ZipCode";
    private final String prefix = "parentMailing";
    private final String TOKEN_BASE_URL;
    private final String ADDRESS_LOCATOR_BASE_URL;
    private final String USER_NAME;
    private final String PASSWORD;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ValidateAddressWithESRI(@Value("${il-gcc.esri-address-api.token-base-url}") String tokenBaseUrl,
                                   @Value("${il-gcc.esri-address-api.address-locator-url}") String addressLocatorUrl,
                                   @Value("${il-gcc.esri-address-api.username}") String userName,
                                   @Value("${il-gcc.esri-address-api.password}") String password) {
        this.TOKEN_BASE_URL = tokenBaseUrl;
        this.ADDRESS_LOCATOR_BASE_URL = addressLocatorUrl;
        this.USER_NAME = userName;
        this.PASSWORD = password;
    }

    @Override
    public void run(Submission submission) {

      WebClient webClient = WebClient.builder()
              .baseUrl(TOKEN_BASE_URL)
              .build();

      String token = webClient.post()
              .uri(uriBuilder -> uriBuilder
                      .path("/geocoder/tokens")
                      .queryParam("request", "getToken")
                      .queryParam("username", USER_NAME)
                      .queryParam("password", PASSWORD)
                      .build())
              .exchangeToMono(response -> {
                  return response.bodyToMono(String.class);
              })
              .block();

      webClient = WebClient.builder()
              .baseUrl(ADDRESS_LOCATOR_BASE_URL)
              .build();
      String address = webClient.post()
              .uri(uriBuilder -> uriBuilder
                      .path("/geocoder/rest/services/Locators/Advanced_Locator/GeocodeServer/findAddressCandidates")
                      .queryParam("Street", submission.getInputData().get(prefix+streetAddress1Key))
                      .queryParam("City", submission.getInputData().get(prefix+cityKey))
                      .queryParam("State", submission.getInputData().get(prefix+stateKey))
                      .queryParam("ZIP", submission.getInputData().get(prefix+zipCodeKey))
                      .queryParam("f", "JSON")
                      .queryParam("token", token)
                      .queryParam("maxLocations", 1)
                      .build())
              .exchangeToMono(response -> {
                  return response.bodyToMono(String.class);
              })
              .block();

      JsonNode addresses;
      try {
          addresses = objectMapper.readTree(address);
      } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
      }
      if(!addresses.get("candidates").isEmpty()){
          String addr = String.valueOf(addresses.get("candidates").get(0).get("address"));
          addr= addr.substring(1, addr.length()-1);
          String[] addressElements = Arrays.stream(addr.split(","))
                  .map(String::trim)
                  .filter(s -> !s.isEmpty())
                  .toArray(String[]::new);
          submission.getInputData().put(FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATE_ADDRESS+prefix,"true");
          submission.getInputData().put(prefix+streetAddress1Key+FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATED,addressElements[0]);
          submission.getInputData().put(prefix+cityKey+FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATED,addressElements[1]);
          submission.getInputData().put(prefix+stateKey+FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATED,addressElements[2]);
          submission.getInputData().put(prefix+zipCodeKey+FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATED,addressElements[3]);
          submissionRepositoryService.save(submission);
      }

    }
}
