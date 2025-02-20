package org.ilgcc.app.data.ccms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class TransactionFile {
    
    private final String fileName;
    private final String fileType;
    private final String filePayload;
    
    @JsonCreator
    public TransactionFile(
            @JsonProperty("name") String fileName,
            @JsonProperty("type") String fileType,
            @JsonProperty("payload") String filePayload) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePayload = filePayload;
    }
    
    @JsonProperty("name")
    public String getFileName() {
        return fileName;
    }
    
    @JsonProperty("type")
    public String getFileType() {
        return fileType;
    }
    
    @JsonProperty("payload")
    public String getFilePayload() {
        return filePayload;
    }

    @Getter
    public enum FileTypeId {
        APPLICATION_PDF("67936"),
        UPLOADED_DOCUMENT("68752");

        private final String value;

        FileTypeId(String value) {
            this.value = value;
        }
    }
}
