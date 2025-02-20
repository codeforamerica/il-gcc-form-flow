package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum CCMSRequestHeaders {

    OCP_APIM_SUBSCRIPTION_KEY("Ocp-Apim-Subscription-Key");

    private final String value;

    CCMSRequestHeaders(String value) {
        this.value = value;
    }
}
