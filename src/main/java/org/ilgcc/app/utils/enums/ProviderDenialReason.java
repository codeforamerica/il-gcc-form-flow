package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum ProviderDenialReason {
    
    NO_REASON_SELECTED("Provider declined"),
    NO_SPACE("Provider declined: I don't have space for the child(ren)"),
    BAD_START_DATE("Provider declined: I don't agree to the start date"),
    BAD_HOURS("Provider declined: I don't agree to the hours"),
    OTHER("Provider declined: Other");

    private final String pdfValue;

    ProviderDenialReason(String pdfValue) {
        this.pdfValue = pdfValue;
    }
}
