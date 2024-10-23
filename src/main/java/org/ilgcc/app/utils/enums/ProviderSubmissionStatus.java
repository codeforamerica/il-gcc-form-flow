package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum ProviderSubmissionStatus {

    ACTIVE("caring", "provider-response-submit-start.active.header", "provider-response-submit-start.active.notice",
            "provider-response-submit-start.active.button"),
    EXPIRED("orangeClock", "provider-response-submit-start.expired.header", "provider-response-submit-start.expired.notice",
            "provider-response-submit-start.expired.button"),
    RESPONDED("docValidation", "provider-response-submit-start.responded.header",
            "provider-response-submit-start.responded.notice", "provider-response-submit-start.responded.button");

    private final String icon;
    private final String headerLabel;
    private final String noticeLabel;
    private final String buttonLabel;

    ProviderSubmissionStatus(String icon, String headerLabel, String noticeLabel, String buttonLabel) {
        this.icon = icon;
        this.headerLabel = headerLabel;
        this.noticeLabel = noticeLabel;
        this.buttonLabel = buttonLabel;
    }
}


