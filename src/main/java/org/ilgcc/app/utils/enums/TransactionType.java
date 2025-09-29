package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    APPLICATION("application"),
    RESUBMISSION("resubmit-app");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }
}
