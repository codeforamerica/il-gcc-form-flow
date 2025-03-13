package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum SubmissionStatus {

    ACTIVE(true),
    CONFIRMED(true),
    EXPIRED(false),
    RESPONDED(false);

    private final boolean isActive;

    SubmissionStatus(boolean isActive) {
        this.isActive = isActive;
    }
}


