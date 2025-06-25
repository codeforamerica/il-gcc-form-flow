package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum ProviderSubmissionStatus {

    ACTIVE("care", "provider-response-submit-start.active.header", "provider-response-submit-start.active.notice",
            "provider-response-submit-start.active.button"),
    EXPIRED("orange-clock", "provider-response-submit-start.expired.header", "provider-response-submit-start.expired.notice",
            "general.button.return.home"),
    RESPONDED("documents-check", "provider-response-submit-start.responded.header",
            "provider-response-submit-start.responded.notice", "general.button.return.home");

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


