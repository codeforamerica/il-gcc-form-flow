package org.ilgcc.app.utils;

import lombok.Getter;

@Getter
public enum LanguageOption implements InputOption {

    ENGLISH("general.language.english", "English", null),
    SPANISH("general.language.spanish", "Spanish", "es"),
    ARABIC("العربية", "Arabic", "ar"),
    TAGALOG("general.language.tagalog", "Tagalog", "tl"),
    POLISH("general.language.polish", "Polish", "pl"),
    Chinese("中文", "Chinese", "zh"),
    OTHER("general.other", "Other", null);

    private final String label;
    private final String pdfValue;
    private final String languageSubtag;

    LanguageOption(String label, String pdfValue, String languageSubtag) {
        this.label = label;
        this.pdfValue = pdfValue;
        this.languageSubtag = languageSubtag;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public String getHelpText() {
        return null;
    }

    public static String getPdfValueByName(String name) {
        return LanguageOption.valueOf(name).pdfValue;
    }
}
