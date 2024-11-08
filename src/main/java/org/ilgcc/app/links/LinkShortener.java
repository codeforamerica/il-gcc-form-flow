package org.ilgcc.app.links;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;


public class LinkShortener implements ShortLinkService {

    @Value("${link-shortener.short-io.api-key}")
    static String apiKey;

    @Value("${link-shortener.short-io.domain}")
    static String domain;

    private static MediaType mediaType = MediaType.parse("application/json");

    private static OkHttpClient client = new OkHttpClient();

    @Override
    public String getShortLink(String longLink) {
        return null;
    }

    @Override
    public ShortenedLinks getShortLinks(String emailLongLink, String textLongLink, String clipboardLongLink) {
        JsonObject content = new JsonObject();

        //do we actually want to pass utmSource here or before?

        try {
            content.addProperty("skipQS", false);
            content.addProperty("archived", false);
            content.addProperty("allowDuplicates", false);
            content.addProperty("domain", domain);

            List links = new ArrayList<>();
            JsonObject emailLink = new JsonObject();
            emailLink.addProperty("originalURL", emailLongLink);
            emailLink.addProperty("utmSource", "email");
            emailLink.addProperty("cloaking", true);
            links.add(emailLink);

            JsonObject textLink = new JsonObject();
            textLink.addProperty("originalURL", textLongLink);
            textLink.addProperty("utmSource", "text");
            textLink.addProperty("cloaking", true);
            links.add(textLink);

            JsonObject clipboardLink = new JsonObject();
            clipboardLink.addProperty("originalURL", clipboardLongLink);
            clipboardLink.addProperty("utmSource", "copy_link");
            clipboardLink.addProperty("cloaking", true);
            links.add(clipboardLink);

            content.addProperty("links", links.toString());

            RequestBody body = RequestBody.create(mediaType, content.toString());


            Request request = new Request.Builder().url("https://api.short.io/links/bulk").post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", apiKey).build();


            Response response = client.newCall(request).execute();


            if (response.code() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.code());
            }

            //todo: fogure out what this actually returns
            ShortenedLinks shortenedLinks = new ShortenedLinks(response.body().toString(), response.body().toString(), response.body().toString());

            return shortenedLinks;


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
