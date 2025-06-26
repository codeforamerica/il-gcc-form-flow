package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum SubmissionStatus {

    ACTIVE(true),
    EXPIRED(false),
    RESPONDED(false),
    INACTIVE(false),
    INCOMPLETE(true),;

    private final boolean isActive;

    SubmissionStatus(boolean isActive) {
        this.isActive = isActive;
    }
}


