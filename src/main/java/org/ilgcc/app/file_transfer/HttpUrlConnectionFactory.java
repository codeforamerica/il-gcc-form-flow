package org.ilgcc.app.file_transfer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.ilgcc.app.config.DocumentTransferConfiguration;
import org.springframework.stereotype.Component;

@Component
public class HttpUrlConnectionFactory {

    private final String consumerId;
    private final String authToken;

    public HttpUrlConnectionFactory(DocumentTransferConfiguration documentTransferConfiguration) {
        this.consumerId = documentTransferConfiguration.getConsumerId();
        this.authToken = documentTransferConfiguration.getAuthToken();
    }

    public HttpURLConnection createHttpURLConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", String.format("Bearer realm=\"%s\" %s", consumerId, authToken));
        connection.setDoOutput(true);
        return connection;
    }
}
