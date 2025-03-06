package org.ilgcc.app.data.ccms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;

public class CCMSTransaction {
    private final String transmissionType;
    private final UUID submissionId;
    private final String clientConfirmationCode;
    private final String submissionOrgId;
    private final String submissionFirstName;
    private final String submissionLastName;
    private final String submissionDOB;
    private final List<String> providerId;
    private final List<TransactionFile> files;
    private final String timestamp;
    private final String webSubmissionTimestamp;

    @JsonCreator
    public CCMSTransaction(
            @JsonProperty("trans_type") String transmissionType,
            @JsonProperty("submission_id") UUID submissionId,
            @JsonProperty("client_confirmation_code") String clientConfirmationCode,
            @JsonProperty("submission_org_id") String submissionOrgId,
            @JsonProperty("submission_name_first") String submissionFirstName,
            @JsonProperty("submission_name_last") String submissionLastName,
            @JsonProperty("submission_dob") String submissionDOB, 
            @JsonProperty("files") List<TransactionFile> files,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("web_submission_timestamp") String webSubmissionTimestamp) {
        this.transmissionType = transmissionType;
        this.submissionId = submissionId;
        this.clientConfirmationCode = clientConfirmationCode;
        this.submissionOrgId = submissionOrgId;
        this.submissionFirstName = submissionFirstName;
        this.submissionLastName = submissionLastName;
        this.submissionDOB = submissionDOB;
        this.providerId = List.of("");
        this.files = files;
        this.timestamp = timestamp;
        this.webSubmissionTimestamp = webSubmissionTimestamp;
    }
    
    @JsonProperty("trans_type")
    public String getTransmissionType() {
        return transmissionType;
    }
    
    @JsonProperty("submission_id")
    public UUID getSubmissionId() {
        return submissionId;
    }
    
    @JsonProperty("client_confirmation_code")
    public String getClientConfirmationCode() {
        return clientConfirmationCode;
    }
    
    @JsonProperty("submission_org_id")
    public String getSubmissionOrgId() {
        return submissionOrgId;
    }
    
    @JsonProperty("submission_name_first")
    public String getSubmissionFirstName() {
        return submissionFirstName;
    }
    
    @JsonProperty("submission_name_last")
    public String getSubmissionLastName() {
        return submissionLastName;
    }
    
    @JsonProperty("submission_dob")
    public String getSubmissionDOB() {
        return submissionDOB;
    }
    
    @JsonProperty("provider_id")
    public List<String> getProviderId() {
        return providerId;
    }
    
    @JsonProperty("files")
    public List<TransactionFile> getFiles() {
        return files;
    }
    
    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }
    
    @JsonProperty("web_submission_timestamp")
    public String getWebSubmissionTimestamp() {
        return webSubmissionTimestamp;
    }
    
    public static enum FileTypeId {
        APPLICATION_PDF("67936"),
        UPLOADED_DOCUMENT("68752");

        private final String value;

        FileTypeId(String value) {
            this.value = value;
        }
    }
    
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "An error occurred when attempting to convert the CCMSTransaction to a String: " + e.getMessage();
        }
    }
}
