package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum CCRRSlug {
    FOUR_C("56522729391679", "4c-ccap-apps"),
    PROJECT_CHILD ("59522729391675", "project-child-ccap-apps"),
    ILLINOIS_ACTION ("47522729391670", "illinois-action-for-children-ccap-apps");

    private final String orgId;
    private final String value;

    CCRRSlug(String orgId, String value) {
        this.orgId = orgId;
        this.value = value;
    }
}


