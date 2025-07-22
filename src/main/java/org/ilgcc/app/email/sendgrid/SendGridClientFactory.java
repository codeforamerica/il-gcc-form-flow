package org.ilgcc.app.email.sendgrid;

import com.sendgrid.Client;
import com.sendgrid.SendGrid;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class SendGridClientFactory {

    private static final int MAX_TOTAL_CONNECTIONS = 20;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 10;
    private static final int CONNECTION_TIMEOUT_MS = 10_000;
    private static final int SOCKET_TIMEOUT_MS = 10_000;

    private static SendGrid instance;

    public static synchronized SendGrid getSendGridClient() {
        if (instance == null) {
            instance = buildSendGridClient(System.getenv("SENDGRID_API_KEY"));
        }
        return instance;
    }

    private static SendGrid buildSendGridClient(String apiKey) {
        // Connection pool
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);

        // Timeouts
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setSocketTimeout(SOCKET_TIMEOUT_MS).build();

        // HttpClient with pooling and timeouts
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig).build();

        // SendGrid Client using the custom HttpClient
        Client client = new Client(httpClient);
        return new SendGrid(apiKey, client);
    }
}
