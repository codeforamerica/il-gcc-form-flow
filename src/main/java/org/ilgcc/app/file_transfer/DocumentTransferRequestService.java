package org.ilgcc.app.file_transfer;

import static org.ilgcc.app.utils.enums.TransmissionStatus.*;

import com.google.gson.Gson;
import formflow.library.data.Submission;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.config.DocumentTransferConfiguration;
import org.ilgcc.app.data.Transmission;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DocumentTransferRequestService {
    
    private final String documentTransferServiceUrl;
    private final String processingOrg;
    private final TransmissionRepositoryService transmissionRepositoryService;
    private final HttpUrlConnectionFactory httpUrlConnectionFactory;

    public DocumentTransferRequestService(
            DocumentTransferConfiguration documentTransferConfiguration,
            TransmissionRepositoryService transmissionRepositoryService, 
            HttpUrlConnectionFactory httpUrlConnectionFactory) {
        this.documentTransferServiceUrl = documentTransferConfiguration.getUrl();
        this.processingOrg = documentTransferConfiguration.getProcessingOrg();
        this.transmissionRepositoryService = transmissionRepositoryService;
        this.httpUrlConnectionFactory = httpUrlConnectionFactory;
    }

    public void sendDocumentTransferServiceRequest(String presignedUrl, Submission submission, String fileName, Transmission transmission) throws IOException {
        HttpURLConnection httpUrlConnection = httpUrlConnectionFactory.createHttpURLConnection(new URL(documentTransferServiceUrl));
        String jsonString = createJsonRequestBody(presignedUrl, submission, fileName);
        try (OutputStream os = httpUrlConnection.getOutputStream()) {
            byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            
            transmissionRepositoryService.updateStatus(transmission, Queued);
        } catch (Exception e) {
            String errorMessage = String.format("There was an error when sending the request for transmission with ID %s of type %s with submission ID %s: ", transmission.getTransmissionId(), transmission.getType(), transmission.getSubmissionId());
            transmissionRepositoryService.setFailureError(transmission, errorMessage + e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(httpUrlConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;

            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            if (httpUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                transmissionRepositoryService.updateStatus(transmission, Complete);
                log.info("Received response from the document transfer service: " + response);
            } else {
                String errorMessage = String.format("The Document Transfer Service responded with Non 200 OK response for transmission with ID %s of type %s with submission ID %s. Response: %s", transmission.getTransmissionId(), transmission.getType(), transmission.getSubmissionId(), response);
                transmissionRepositoryService.setFailureError(transmission, errorMessage);
                throw new RuntimeException(errorMessage);
            }
            transmissionRepositoryService.updateStatus(transmission, Complete);
            log.info("Received response from the document transfer service: " + response);
        } catch (Exception e) {
            String errorMessage = String.format("There was an error when receiving the a response from the Document Transfer Service for transmission with ID %s of type %s with submission ID %s: ", transmission.getTransmissionId(), transmission.getType(), transmission.getSubmissionId());
            transmissionRepositoryService.setFailureError(transmission, errorMessage + e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }

    public String createJsonRequestBody(String presignedUrl, Submission submission, String fileName) {
        Map<String, Object> jsonRequestBodyMap = new HashMap<>();
        Map<String, String> sourceMapBody = new HashMap<>();
        sourceMapBody.put("type", "url");
        sourceMapBody.put("url", presignedUrl);
        jsonRequestBodyMap.put("source", sourceMapBody);

        Map<String, Object> destinationMap = new HashMap<>();
        destinationMap.put("type", "onedrive");
        destinationMap.put("path", FileNameUtility.getSharePointFilePath(submission, processingOrg));
        destinationMap.put("filename", fileName);
        jsonRequestBodyMap.put("destination", destinationMap);

        Gson gson = new Gson();
        return gson.toJson(jsonRequestBodyMap);
    }
}
