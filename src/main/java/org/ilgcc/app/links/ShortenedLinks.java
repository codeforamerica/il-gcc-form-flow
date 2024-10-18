package org.ilgcc.app.links;

import lombok.Getter;

@Getter
public class ShortenedLinks {

    private final String shortEmailLink;

    private final String shortTextLink;

    private final String shortClipboardLink;

    public ShortenedLinks(String shortEmailLink, String shortTextLink, String shortClipboardLink) {
        this.shortEmailLink = shortEmailLink;
        this.shortTextLink = shortTextLink;
        this.shortClipboardLink = shortClipboardLink;
    }

}
