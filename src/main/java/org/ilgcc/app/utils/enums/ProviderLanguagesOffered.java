package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter

public enum ProviderLanguagesOffered {
  ENGLISH("registration-service-languages.provider-languages.english", "English", "ENGLISH", ""),
  SPANISH("registration-service-languages.provider-languages.spanish" ,"Spanish", "SPANISH", ""),
  POLISH("registration-service-languages.provider-languages.polish","Polish", "POLISH", ""),
  CHINESE("registration-service-languages.provider-languages.chinese" ,"Chinese", "CHINESE", ""),
  TAGALOG("registration-service-languages.provider-languages.tagalog", "Tagalog","OTHER",
      "Tagalog (including Filipino)"),
  HINDI("registration-service-languages.provider-languages.hindi", "Hindi","OTHER", "Hindi"),
  ARABIC("registration-service-languages.provider-languages.arabic", "Arabic","OTHER", "Arabic");
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

