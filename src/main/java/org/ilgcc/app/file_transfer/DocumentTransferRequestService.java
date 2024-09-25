package org.ilgcc.app.file_transfer;

import static org.ilgcc.app.utils.enums.status.*;
import static org.ilgcc.app.utils.enums.status.Complete;
import static org.ilgcc.app.utils.enums.status.Failed;

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
import org.ilgcc.app.db.Transmission;
import org.ilgcc.app.utils.enums.FileNameUtility;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DocumentTransferRequestService {

    private final String consumerId;
    private final String consumerAuthToken;
    private final String documentTransferServiceUrl;
    private final String processingOrg;

    public DocumentTransferRequestService(
            DocumentTransferConfiguration documentTransferConfiguration) {
        consumerId = documentTransferConfiguration.getConsumerId();
        consumerAuthToken = documentTransferConfiguration.getAuthToken();
        documentTransferServiceUrl = documentTransferConfiguration.getUrl();
        processingOrg = documentTransferConfiguration.getProcessingOrg();
    }

    public void sendDocumentTransferServiceRequest(String presignedUrl, Submission submission, String fileName, Transmission transmission) throws IOException {
        HttpURLConnection httpUrlConnection = getHttpURLConnection();
        String jsonString = createJsonRequestBody(presignedUrl, submission, fileName);
        try (OutputStream os = httpUrlConnection.getOutputStream()) {
            byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            
            transmission.setStatus(Queued);
        } catch (Exception e) {
            transmission.setStatus(Failed);
            Map<Integer, String> errors = transmission.getErrors();
            String errorMessage = String.format("There was an error when sending the request for transmission with ID %s of type %s with submission ID %s: ", transmission.getTransmissionId(), transmission.getType(), transmission.getSubmissionId());
            errors.put(transmission.getAttempts(), errorMessage + e.getMessage());
            transmission.setErrors(errors);
            transmission.setAttempts(transmission.getAttempts() + 1);
            throw new RuntimeException(errorMessage, e);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(httpUrlConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;

            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            
            transmission.setStatus(Complete);
            log.info("Received response from the document transfer service: " + response);
        } catch (Exception e) {
            transmission.setStatus(Failed);
            Map<Integer, String> errors = transmission.getErrors();
            String errorMessage = String.format("There was an error when receiving the a response from the Document Transfer Service for transmission with ID %s of type %s with submission ID %s: ", transmission.getTransmissionId(), transmission.getType(), transmission.getSubmissionId());
            errors.put(transmission.getAttempts(), errorMessage + e.getMessage());
            transmission.setErrors(errors);
            transmission.setAttempts(transmission.getAttempts() + 1);
            throw new RuntimeException(errorMessage, e);
        }
    }


    @NotNull
    public HttpURLConnection getHttpURLConnection() throws IOException {
        URL dtsURL = new URL(documentTransferServiceUrl);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) dtsURL.openConnection();
        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setRequestProperty("Content-Type", "application/json");
        httpUrlConnection.setRequestProperty("Accept", "application/json");
        httpUrlConnection.setRequestProperty("Authorization", String.format("Bearer realm=\"%s\" %s", consumerId, consumerAuthToken));
        httpUrlConnection.setDoOutput(true);
        return httpUrlConnection;
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
