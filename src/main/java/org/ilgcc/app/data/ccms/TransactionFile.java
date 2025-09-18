package org.ilgcc.app.data.ccms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import formflow.library.data.UserFile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class TransactionFile {
    
    private final String fileName;
    private final String fileType;
    private final String filePayload;
    
    @Getter
    @Setter
    @JsonIgnore
    private UserFile userFile;
    
    @JsonCreator
    public TransactionFile(
            @JsonProperty("name") String fileName,
            @JsonProperty("type") String fileType,
            @JsonProperty("payload") String filePayload) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePayload = filePayload;
        this.userFile = null;
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
