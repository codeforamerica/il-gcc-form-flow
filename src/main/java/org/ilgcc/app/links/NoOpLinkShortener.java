package org.ilgcc.app.links;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpLinkShortener implements ShortLinkService {

    @Override
    public String getShortLink(String longLink) {
        return getShortLink(longLink, 3);
    }

    public String getShortLink(String longLink, int delay) {
        log.debug("Not actually shortening: {}", longLink);
        for (int i = 0; i < delay; i++) {
            log.debug("Waiting 1 more second while doing nothing with: {}", longLink);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("Successfully did absolutely nothing of value with: {}", longLink);
        return longLink;
    }

    public ShortenedLinks getShortLinks(String emailLongLink, String textLongLink, String clipboardLongLink) {

        CompletableFuture<String> shortEmailUrl = CompletableFuture.supplyAsync(() -> {
            String shortUrl = getShortLink(emailLongLink, 3);
            log.info("Shortened email url of {} to be {}", emailLongLink, shortUrl);
            return shortUrl;
        }).exceptionally(e -> {
            log.error("Error shortening email link {}", emailLongLink, e);
            return emailLongLink;
        });

        CompletableFuture<String> shortTextUrl = CompletableFuture.supplyAsync(() -> {
            String shortUrl = getShortLink(textLongLink, 10);
            log.info("Shortened text url of {} to be {}", textLongLink, shortUrl);
            return shortUrl;
        }).exceptionally(e -> {
            log.error("Error shortening text link {}", textLongLink, e);
            return textLongLink;
        });

        CompletableFuture<String> shortClipboardUrl = CompletableFuture.supplyAsync(() -> {
            String shortUrl = getShortLink(clipboardLongLink, 5);
            log.info("Shortened clipboard url of {} to be {}", clipboardLongLink, shortUrl);
            return shortUrl;
        }).exceptionally(e -> {
            log.error("Error shortening clipboard link {}", clipboardLongLink, e);
            return clipboardLongLink;
        });

        final CompletableFuture<Void> completableFutureAllOf = CompletableFuture.allOf(shortEmailUrl, shortTextUrl,
                shortClipboardUrl);

        final CompletableFuture<ShortenedLinks> shortenedLinks = completableFutureAllOf.thenApply(
                (voidInput) -> new ShortenedLinks(shortEmailUrl.join(), shortTextUrl.join(), shortClipboardUrl.join()));

        return shortenedLinks.join();
    }
}
