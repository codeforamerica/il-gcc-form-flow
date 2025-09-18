package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    
    NOT_STARTED("Not Started"),
    REQUESTED("Requested"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    MANUALLY_COMPLETED("Manually Completed");
    
    private final String status;
    
    TransactionStatus(String status) {
        this.status = status;
    }
}
