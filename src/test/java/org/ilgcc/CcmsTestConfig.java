package org.ilgcc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import org.ilgcc.app.config.CCMSApiConfiguration;
import org.ilgcc.app.data.ccms.CCMSApiClient;
import org.ilgcc.app.data.ccms.CCMSTransaction;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class CcmsTestConfig {

    @Bean
    @Primary
    CCMSApiConfiguration ccmsApiConfiguration() {
        var cfg = new CCMSApiConfiguration();
        cfg.setTransactionDelayMinutes(0);
        cfg.setOfflineTransactionDelayOffset(0);
        cfg.setCcmsOfflineTimeRanges(List.of());
        return cfg;
    }

    @Bean
    CCMSApiClient ccmsApiClient(CCMSApiConfiguration cfg) throws JsonProcessingException {
        var client = mock(CCMSApiClient.class);

        when(client.getConfiguration()).thenReturn(cfg);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode()
                .put("transactionId", "12345678-9123-1234-3456-123456789123")
                .put("workItemId", "test-work-item");

        when(client.sendRequest(anyString(), any(CCMSTransaction.class)))
                .thenReturn(response);

        return client;
    }
}
