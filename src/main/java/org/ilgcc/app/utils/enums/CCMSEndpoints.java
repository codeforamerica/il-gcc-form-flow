package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum CCMSEndpoints {

    APP_SUBMISSION_ENDPOINT("appSubmission"),
    WORK_ITEM_LOOKUP_ENDPOINT("fetch");

    private final String value;

    CCMSEndpoints(String value) {
        this.value = value;
    }
}
