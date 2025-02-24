package org.ilgcc.app.file_transfer;

import static org.ilgcc.app.utils.enums.TransmissionStatus.Complete;

import com.google.gson.Gson;
import formflow.library.data.Submission;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.config.DocumentTransferConfiguration;
import org.ilgcc.app.data.Transmission;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.utils.FileNameUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DocumentTransferRequestService implements DocumentTransferRequest {
    
    private final String documentTransferServiceUrl;
    private final TransmissionRepositoryService transmissionRepositoryService;
    private final HttpUrlConnectionFactory httpUrlConnectionFactory;

    @Autowired
    Environment environment;

    public DocumentTransferRequestService(
            DocumentTransferConfiguration documentTransferConfiguration,
            TransmissionRepositoryService transmissionRepositoryService, 
            HttpUrlConnectionFactory httpUrlConnectionFactory) {
        this.documentTransferServiceUrl = documentTransferConfiguration.getUrl();
        this.transmissionRepositoryService = transmissionRepositoryService;
        this.httpUrlConnectionFactory = httpUrlConnectionFactory;
    }
    
    @Override
    public void sendDocumentTransferServiceRequest(String presignedUrl, Submission submission, String fileName, UUID transmissionId)
            throws IOException, URISyntaxException {
        Transmission transmission = transmissionRepositoryService.findById(transmissionId);
        HttpURLConnection httpUrlConnection = httpUrlConnectionFactory.createHttpURLConnection(new URI(documentTransferServiceUrl).toURL());
        String jsonString = createJsonRequestBody(presignedUrl, submission, fileName);

        try (OutputStream os = httpUrlConnection.getOutputStream()) {
            byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            
        } catch (Exception e) {
            String errorMessage = String.format("There was an error when sending the request for transmission with ID %s of type %s with submission ID %s: ", transmission.getTransmissionId(), transmission.getType(), transmission.getSubmissionId());
            transmissionRepositoryService.setFailureError(transmission, errorMessage + e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }

        try (BufferedReader br = new BufferedReader (
                new InputStreamReader(httpUrlConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;

            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            int responseCode = httpUrlConnection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                transmissionRepositoryService.updateStatus(transmission, Complete);
                log.info("Received response from the document transfer service: " + response);
            } else {
                String errorMessage = String.format("The Document Transfer Service responded with Non 200 OK response for transmission with ID %s of type %s with submission ID %s. Response: %s", transmission.getTransmissionId(), transmission.getType(), transmission.getSubmissionId(), response);
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = String.format("There was an error when receiving the a response from the Document Transfer Service for transmission with ID %s of type %s with submission ID %s: ", transmission.getTransmissionId(), transmission.getType(), transmission.getSubmissionId());
            transmissionRepositoryService.setFailureError(transmission, errorMessage + e.getMessage());
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    @Override
    public String createJsonRequestBody(String presignedUrl, Submission submission, String fileName) {
        Map<String, Object> jsonRequestBodyMap = new HashMap<>();
        Map<String, String> sourceMapBody = new HashMap<>();
        sourceMapBody.put("type", "url");
        sourceMapBody.put("url", presignedUrl);
        jsonRequestBodyMap.put("source", sourceMapBody);

        Map<String, Object> destinationMap = new HashMap<>();
        destinationMap.put("type", "onedrive");
        destinationMap.put("path", FileNameUtility.getSharePointFilePath(submission, isProductionEnvironment()));
        destinationMap.put("filename", fileName);
        jsonRequestBodyMap.put("destination", destinationMap);

        Gson gson = new Gson();
        return gson.toJson(jsonRequestBodyMap);
    }

    private boolean isProductionEnvironment(){
       return Arrays.asList(this.environment.getActiveProfiles()).contains("production");
    }
}
