package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum ProviderSubmissionStatus {

    ACTIVE("caring", "provider-responses-submit-start.active.header", "provider-responses-submit-start.active.notice",
            "provider-responses-submit-start.active.button", ""),
    EXPIRED("orangeClock", "provider-responses-submit-start.expired.header", "provider-responses-submit-start.expired.notice",
            "provider-responses-submit-start.expired.button", "provider-responses-submit-start.expired.subtext"),
    RESPONDED("docValidation", "provider-responses-submit-start.responded.header",
            "provider-responses-submit-start.responded.notice", "provider-responses-submit-start.responded.button", "");

    private final String icon;
    private final String headerLabel;
    private final String noticeLabel;
    private final String buttonLabel;
    private final String subtextLabel;

    ProviderSubmissionStatus(String icon, String headerLabel, String noticeLabel, String buttonLabel, String subtextLabel) {
        this.icon = icon;
        this.headerLabel = headerLabel;
        this.noticeLabel = noticeLabel;
        this.buttonLabel = buttonLabel;
        this.subtextLabel = subtextLabel;
    }
}


