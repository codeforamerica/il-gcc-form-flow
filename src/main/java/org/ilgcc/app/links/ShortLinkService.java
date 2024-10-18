package org.ilgcc.app.links;

public interface ShortLinkService {

    public String getShortLink(String longLink);

    public ShortenedLinks getShortLinks(String emailLongLink, String textLongLink, String clipboardLongLink);

}
