package org.ilgcc.jobs;

import formflow.library.data.Submission;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.config.DocumentTransferConfiguration;
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
    private String BUCKET_NAME;
    private Region region;
    private String secretKey;
    private String accessKey;
    private String consumerId;
    private String consumerAuthToken;
    private final String documentTransferServiceUrl;

    public PdfTransmissionJobService(@Value("${form-flow.aws.access_key}") String accessKey,
            @Value("${form-flow.aws.secret_key}") String secretKey,
            @Value("${form-flow.aws.s3_bucket_name}") String BUCKET_NAME,
            @Value("${form-flow.aws.region}") String region,
            DocumentTransferConfiguration documentTransferConfiguration) throws MalformedURLException {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.BUCKET_NAME = BUCKET_NAME;
        this.region = Region.of(region);
        this.documentTransferServiceUrl = documentTransferConfiguration.getUrl();
        this.consumerId = documentTransferConfiguration.getConsumerId();
        this.consumerAuthToken = documentTransferConfiguration.getAuthToken();

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(this.accessKey, this.secretKey);
        this.s3Presigner = S3Presigner.builder()
                .region(this.region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public void enqueuePdfTransmissionJob(String objectKey, Submission submission) throws IOException {
        String presignedUrl = generatePresignedUrl(objectKey);
        log.info("Generated presigned URL: {} for object: {}", presignedUrl, objectKey);
        sendDocumentTransferServiceRequest(presignedUrl, submission);
    }

    private String generatePresignedUrl(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(objectKey)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
    }

    private void sendDocumentTransferServiceRequest(String presignedUrl, Submission submission) throws IOException {
        URL dtsURL = new URL(documentTransferServiceUrl);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) dtsURL.openConnection();
        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setRequestProperty("Content-Type", "application/json");
        httpUrlConnection.setRequestProperty("Accept", "application/json");
        httpUrlConnection.setRequestProperty("Authorization", String.format("Bearer realm=\"%s\" %s", consumerId, consumerAuthToken));
        httpUrlConnection.setDoOutput(true);

        String jsonString = String.format("{"
                + "\"source\": {"
                + "\"type\": \"url\","
                + "\"url\": \"%s\""
                + "},"
                + "\"destination\": {"
                + "\"type\": \"onedrive\","
                + "\"path\": \"document/%s\","
                + "\"filename\": \"%s.zip\""
                + "}"
                + "}", presignedUrl, submission.getId(), submission.getId());
        
        log.info("JSON Request: " + jsonString);

        try (OutputStream os = httpUrlConnection.getOutputStream()) {
            byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (Exception e) {
            log.error("There was an error when sending the request to the document transfer service", e);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(httpUrlConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            log.info("Received response from the document transfer service: {}", response);
        } catch (Exception e) {
            log.error("There was an error when reading the response from the document transfer service", e);
        }
    }
}
