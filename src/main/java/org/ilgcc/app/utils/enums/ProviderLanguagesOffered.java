package org.ilgcc.app.utils.enums;

public enum ProviderLanguagesOffered {
  ENGLISH("English"),
  SPANISH("Spanish"),
  POLISH("Polish"),
  CHINESE("Chinese"),
  TAGALOG("Tagalog"),
  HINDI("Hindi"),
  ARABIC("Arabic");
  private final String value;
  private ProviderLanguagesOffered(String value) {
    this.value = value;
  }
}

