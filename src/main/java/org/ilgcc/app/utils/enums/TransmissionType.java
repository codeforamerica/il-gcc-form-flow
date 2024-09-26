package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum TransmissionType {
    APPLICATION_PDF("Application PDF"),
    UPLOADED_DOCUMENT("Uploaded Document");
    
    private final String type;
    
    TransmissionType(String type) {
        this.type = type;
    }
}


