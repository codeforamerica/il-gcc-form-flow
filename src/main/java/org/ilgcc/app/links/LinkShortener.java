package org.ilgcc.app.links;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LinkShortener implements ShortLinkService {

    private static MediaType mediaType = MediaType.parse("application/json");

    private static OkHttpClient client = new OkHttpClient();

    @Override
    public String getShortLink(String longLink) {
        return null;
    }

    @Override
    public ShortenedLinks getShortLinks(String emailLongLink, String textLongLink, String clipboardLongLink) {
        String emailLink = requestShortLink(emailLongLink, "email");
        String textLink = requestShortLink(textLongLink, "text");
        String clipboardLink = requestShortLink(clipboardLongLink, "copy_link");

        return new ShortenedLinks(emailLink, textLink, clipboardLink);
    }

    public String requestShortLink(String longLink, String utmSource) {
        JsonObject content = new JsonObject();

        try {
            content.addProperty("skipQS", false);
            content.addProperty("archived", false);
            content.addProperty("allowDuplicates", false);
            content.addProperty("domain", System.getenv("SHORT_IO_DOMAIN"));
            content.addProperty("originalURL", longLink);
            content.addProperty("utmSource", utmSource);

            RequestBody body = RequestBody.create(mediaType, content.toString());

            Request request = new Request.Builder()
                    .url("https://api.short.io/links")
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", System.getenv("SHORT_IO_API_KEY"))
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                log.error(String.format("Failed : HTTP error code : %s", response.code()));
                return longLink;
            }
            JsonObject jsonbody = JsonParser.parseString(response.body().string()).getAsJsonObject();
            return jsonbody.get("shortURL").getAsString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
