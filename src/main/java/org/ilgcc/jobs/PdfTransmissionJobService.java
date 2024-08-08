package org.ilgcc.jobs;

import static org.ilgcc.app.utils.SubmissionUtilities.getDashFormattedSubmittedAtDate;

import com.google.gson.Gson;

import formflow.library.data.Submission;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.config.DocumentTransferConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Service
public class PdfTransmissionJobService {

    private final S3Presigner s3Presigner;
    private final String BUCKET_NAME;
    private final Region region;
    private final String secretKey;
    private final String accessKey;
    private final String consumerId;
    private final String consumerAuthToken;
    private final String documentTransferServiceUrl;
    private final Long presignedUrlDuration;
    private final String processingOrg;
    private final JobScheduler jobScheduler;

    public PdfTransmissionJobService(@Value("${form-flow.aws.access_key}") String accessKey,
            @Value("${form-flow.aws.secret_key}") String secretKey,
            @Value("${form-flow.aws.s3_bucket_name}") String BUCKET_NAME,
            @Value("${form-flow.aws.region}") String region,
            DocumentTransferConfiguration documentTransferConfiguration, 
            JobScheduler jobScheduler) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.BUCKET_NAME = BUCKET_NAME;
        this.region = Region.of(region);
        this.documentTransferServiceUrl = documentTransferConfiguration.getUrl();
        this.consumerId = documentTransferConfiguration.getConsumerId();
        this.consumerAuthToken = documentTransferConfiguration.getAuthToken();
        this.presignedUrlDuration = documentTransferConfiguration.getPresignedUrlDuration();
        this.processingOrg = documentTransferConfiguration.getProcessingOrg();
        this.jobScheduler = jobScheduler;

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(this.accessKey, this.secretKey);
        this.s3Presigner = S3Presigner.builder()
                .region(this.region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public void enqueuePdfTransmissionJob(String objectKey, Submission submission) throws IOException {
        String presignedUrl = generatePresignedUrl(objectKey);
        JobId jobId = jobScheduler.enqueue(() -> sendDocumentTransferServiceRequest(presignedUrl, submission));
        log.info("Enqueued job with ID: {} for submission with ID: {}", jobId, submission.getId());
    }

    public String generatePresignedUrl(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(objectKey)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignedUrlDuration))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
    }
    
    @Job(name = "Send Document Transfer Service Request", retries = 3)
    public void sendDocumentTransferServiceRequest(String presignedUrl, Submission submission) throws IOException {
        HttpURLConnection httpUrlConnection = getHttpURLConnection();
        String jsonString = createJsonRequestBody(presignedUrl, submission);
        try (OutputStream os = httpUrlConnection.getOutputStream()) {
            byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (Exception e) {
            throw new RuntimeException("There was an error when sending the request to the document transfer service for submission with ID: " + submission.getId(), e);
        }
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(httpUrlConnection.getInputStream(), StandardCharsets.UTF_8))) {
            test();
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            
            log.info("Received response from the document transfer service: " + response);
        } catch (Exception e) {
            throw new RuntimeException("There was an error when reading the response from the document transfer service for submission with ID: " + submission.getId(), e);
        }
    }

    private String createJsonRequestBody(String presignedUrl, Submission submission) {
        Map<String, Object> jsonRequestBodyMap = new HashMap<>();
        Map<String, String> sourceMapBody = new HashMap<>();
        sourceMapBody.put("type", "url");
        sourceMapBody.put("url", presignedUrl);
        jsonRequestBodyMap.put("source", sourceMapBody);

        Map<String, Object> destinationMap = new HashMap<>();
        destinationMap.put("type", "onedrive");
        destinationMap.put("path", String.format("/%s/%s", processingOrg, getDashFormattedSubmittedAtDate(submission)));
        destinationMap.put("filename", submission.getId() + ".pdf");
        jsonRequestBodyMap.put("destination", destinationMap);

        Gson gson = new Gson();
        return gson.toJson(jsonRequestBodyMap);
    }

    @NotNull
    private HttpURLConnection getHttpURLConnection() throws IOException {
        URL dtsURL = new URL(documentTransferServiceUrl);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) dtsURL.openConnection();
        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setRequestProperty("Content-Type", "application/json");
        httpUrlConnection.setRequestProperty("Accept", "application/json");
        httpUrlConnection.setRequestProperty("Authorization", String.format("Bearer realm=\"%s\" %s", consumerId, consumerAuthToken));
        httpUrlConnection.setDoOutput(true);
        return httpUrlConnection;
    }
    
    private RuntimeException test() {
        throw new RuntimeException("This is a test exception");
    }
}
