package org.ilgcc.app.links;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpLinkShortener implements ShortLinkService {

    @Override
    public String getShortLink(String longLink) {
        log.info("Not actually shortening: {}", longLink);
        for (int i = 0; i < 3; i++) {
            log.info("Waiting 1 second while doing nothing with: {}", longLink);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        longLink = longLink + "&test_attribute=foo";
        log.info("Successfully did absolutely nothing of value with: {}", longLink);
        return longLink;
    }
}
