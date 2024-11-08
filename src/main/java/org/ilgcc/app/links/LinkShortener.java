package org.ilgcc.app.links;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LinkShortener {

    private static String apiKey;
    private static String domain;

    private static MediaType mediaType = MediaType.parse("application/json");

    private static OkHttpClient client = new OkHttpClient();

    public LinkShortener(@Value("${il-gcc.link-shortener.api-key}") String apiKey,
            @Value("${il-gcc.link-shortener.domain}") String domain) {
        this.apiKey = apiKey;
        this.domain = domain;
    }

    public static ShortenedLinks getShortLinks(String emailLongLink, String textLongLink, String clipboardLongLink) {
        JsonObject content = new JsonObject();

        try {
            content.addProperty("skipQS", false);
            content.addProperty("archived", false);
            content.addProperty("allowDuplicates", false);
            content.addProperty("domain", domain);

            JsonArray links = new JsonArray();

            JsonObject emailLink = new JsonObject();
            emailLink.addProperty("originalURL", emailLongLink);
            emailLink.addProperty("utmSource", "email");
            emailLink.addProperty("allowDuplicates", false);
            links.add(emailLink);

            JsonObject textLink = new JsonObject();
            textLink.addProperty("originalURL", textLongLink);
            textLink.addProperty("utmSource", "text");
            textLink.addProperty("allowDuplicates", false);
            links.add(textLink);

            JsonObject clipboardLink = new JsonObject();
            clipboardLink.addProperty("originalURL", clipboardLongLink);
            clipboardLink.addProperty("utmSource", "copy_link");
            clipboardLink.addProperty("allowDuplicates", false);
            links.add(clipboardLink);

            content.add("links", links);

            RequestBody body = RequestBody.create(mediaType, content.toString());

            Request request = new Request.Builder()
                    .url("https://api.short.io/links/bulk")
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", apiKey)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                log.error(String.format("Failed : HTTP error code : %s", response.code()));
                return new ShortenedLinks(emailLongLink, textLongLink, clipboardLongLink);
            }

            JsonArray responseJson = (JsonArray) JsonParser.parseString(response.body().string());
            return new ShortenedLinks(responseJson.get(0).getAsJsonObject().get("shortURL").getAsString(), responseJson.get(1).getAsJsonObject().get("shortURL").getAsString(), responseJson.get(2).getAsJsonObject().get("shortURL").getAsString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
