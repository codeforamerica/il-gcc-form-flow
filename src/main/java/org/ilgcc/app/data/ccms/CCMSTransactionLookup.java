package org.ilgcc.app.data.ccms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class CCMSTransactionLookup {
    private final UUID transactionId;

    @JsonCreator
    public CCMSTransactionLookup(@JsonProperty("transactionId") UUID transactionId) {
        this.transactionId = transactionId;
    }

    @JsonProperty("transactionId")
    public UUID getTransactionId() {
        return transactionId;
    }
}
