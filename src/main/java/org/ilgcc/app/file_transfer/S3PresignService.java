package org.ilgcc.app.file_transfer;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.config.DocumentTransferConfiguration;
import org.ilgcc.app.exception.S3ObjectNotFoundException;
import org.ilgcc.app.exception.S3ObjectNotScannedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@Slf4j
public class S3PresignService {

    private final Region region;
    private final String secretKey;
    private final String accessKey;
    private final String bucketName;
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final Long presignedUrlDuration;
    
    public S3PresignService(@Value("${form-flow.aws.access_key}") String accessKey,
            @Value("${form-flow.aws.secret_key}") String secretKey,
            @Value("${form-flow.aws.s3_bucket_name}") String bucketName,
            @Value("${form-flow.aws.region}") String region,
            DocumentTransferConfiguration documentTransferConfiguration) {
        this.region = Region.of(region);
        this.secretKey = secretKey;
        this.accessKey = accessKey;
        this.bucketName = bucketName;
        this.presignedUrlDuration = documentTransferConfiguration.getPresignedUrlDuration();

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(this.accessKey, this.secretKey);
        
        this.s3Client = S3Client.builder()
                .region(this.region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        this.s3Presigner = S3Presigner.builder()
                .region(this.region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public String generatePresignedUrl(String objectPath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectPath)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignedUrlDuration))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
    }
    
    @Async
    @Retryable(
            retryFor = {S3ObjectNotScannedException.class}, 
            maxAttempts = 5, 
            backoff = @Backoff(delay = 5000)
    )
    public CompletableFuture<Boolean> isObjectScannedAndClean(String objectPath) {
        try {
            GetObjectTaggingRequest getObjectTaggingRequest = GetObjectTaggingRequest.builder()
                    .bucket(bucketName)
                    .key(objectPath)
                    .build();
            GetObjectTaggingResponse taggingResult = s3Client.getObjectTagging(getObjectTaggingRequest);
            boolean isClean = taggingResult.tagSet().stream()
                    .anyMatch(tag -> tag.key().equals("scan-result") && tag.value().equals("Clean"));
            if (!isClean) {
                throw new S3ObjectNotScannedException("The file at path: " + objectPath + " did not have tag 'scan-result' it has most likely not yet been scanned.");
            }
            taggingResult.tagSet().stream()
                    .filter(tag -> tag.key().equals("scan-result"))
                    .findFirst()
                    .ifPresent(tag -> log.info("File at path: {} was scanned and found to be clean with 'scan-result' {}", objectPath, tag.value()));
            return CompletableFuture.completedFuture(true);
        } catch (S3Exception e) {
            log.error("No object was found at path" + objectPath + " It's possible the object was moved to quarantine, or deleted.", e);
            throw new S3ObjectNotFoundException("No object was found at path: " + objectPath + " when checking file tags. It's possible the object was moved to quarantine, or deleted.");
        }
    }
    
    @Recover
    public CompletableFuture<Boolean> recover(S3ObjectNotScannedException e, String objectPath) {
        log.error("The file at path: {} was not scanned after 5 attempts. It's likely the file was not sent to SharePoint.", objectPath);
        return CompletableFuture.failedFuture(e);
    }
    
    @Recover CompletableFuture<Boolean> recover(S3ObjectNotFoundException e, String objectPath) {
        log.error("The file at path: {} was not found. It's likely the file had a virus and was quarantined.", objectPath);
        return CompletableFuture.failedFuture(e);
    }
}