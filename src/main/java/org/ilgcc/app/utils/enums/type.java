package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum type {
    APPLICATION_PDF("Application PDF"),
    UPLOADED_DOCUMENT("Uploaded Document");
    
    private final String type;
    
    type(String type) {
        this.type = type;
    }
}


