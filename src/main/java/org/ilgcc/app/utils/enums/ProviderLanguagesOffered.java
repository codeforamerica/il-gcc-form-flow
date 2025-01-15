package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter

public enum ProviderLanguagesOffered {
  ENGLISH("registration-service-languages.provider-languages.english", "english", "ENGLISH", ""),
  SPANISH("registration-service-languages.provider-languages.spanish" ,"spanish", "SPANISH", ""),
  POLISH("registration-service-languages.provider-languages.polish","polish", "POLISH", ""),
  CHINESE("registration-service-languages.provider-languages.chinese" ,"chinese", "CHINESE", ""),
  TAGALOG("registration-service-languages.provider-languages.tagalog", "tagalog","OTHER",
      "Tagalog (including Filipino)"),
  HINDI("registration-service-languages.provider-languages.hindi", "hindi","OTHER", "Hindi"),
  ARABIC("registration-service-languages.provider-languages.arabic", "arabic","OTHER", "Arabic");
  private final String label;
  private final String value;
  private final String providerLanguagePdfFieldValue;
  private final String providerLanguageOtherDetailPdfFieldValue;

  ProviderLanguagesOffered(String label, String value ,String providerLanguagePdfFieldValue, String providerLanguageOtherDetailPdfFieldValue) {
    this.label = label;
    this.value = value;
    this.providerLanguagePdfFieldValue = providerLanguagePdfFieldValue;
    this.providerLanguageOtherDetailPdfFieldValue = providerLanguageOtherDetailPdfFieldValue;
  }

}

