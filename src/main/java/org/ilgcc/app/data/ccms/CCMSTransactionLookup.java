package org.ilgcc.app.data.ccms;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class CCMSTransactionLookup {
    
    @JsonProperty("transactionId")
    private final UUID transactionId;

    public CCMSTransactionLookup(UUID transactionId) {
        this.transactionId = transactionId;
    }
}
