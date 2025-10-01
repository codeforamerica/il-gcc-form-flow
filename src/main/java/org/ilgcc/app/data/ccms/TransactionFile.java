package org.ilgcc.app.data.ccms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import formflow.library.data.UserFile;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionFile {
    
    private final String fileName;
    private final String fileType;
    private final String filePayload;
    private final UUID userFileId;
    
    @Getter
    @Setter
    @JsonIgnore
    private UserFile userFile;
    
    public TransactionFile(
            String fileName,
            String fileType,
            String filePayload,
            UUID userFileId,
            UserFile userFile) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePayload = filePayload;
        this.userFileId = userFileId;
        this.userFile = userFile;
    }
    
    // Overloaded constructor without userFileId -- can be removed when v2 API is productized
    public TransactionFile(String fileName, String fileType, String filePayload, UserFile userFile) {
        this(fileName, fileType, filePayload, null, userFile);
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

    @JsonProperty("id")
    public UUID getUserFileId() { return userFileId; }

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
