package org.ilgcc.app.utils;

import java.util.Arrays;

public enum ZipcodeOption implements InputOption {
    zip_60012("60012"),
    zip_60013("60013"),
    zip_60014("60014"),
    zip_60021("60021"),
    zip_60033("60033"),
    zip_60034("60034"),
    zip_60039("60039"),
    zip_60050("60050"),
    zip_60051("60051"),
    zip_60071("60071"),
    zip_60072("60072"),
    zip_60081("60081"),
    zip_60097("60097"),
    zip_60098("60098"),
    zip_60102("60102"),
    zip_60111("60111"),
    zip_60112("60112"),
    zip_60113("60113"),
    zip_60115("60115"),
    zip_60129("60129"),
    zip_60135("60135"),
    zip_60142("60142"),
    zip_60145("60145"),
    zip_60146("60146"),
    zip_60150("60150"),
    zip_60152("60152"),
    zip_60156("60156"),
    zip_60178("60178"),
    zip_60180("60180"),
    zip_60520("60520"),
    zip_60530("60530"),
    zip_60548("60548"),
    zip_60550("60550"),
    zip_60552("60552"),
    zip_60553("60553"),
    zip_60556("60556"),
    zip_61006("61006"),
    zip_61007("61007"),
    zip_61010("61010"),
    zip_61014("61014"),
    zip_61015("61015"),
    zip_61020("61020"),
    zip_61021("61021"),
    zip_61030("61030"),
    zip_61031("61031"),
    zip_61037("61037"),
    zip_61042("61042"),
    zip_61043("61043"),
    zip_61046("61046"),
    zip_61047("61047"),
    zip_61049("61049"),
    zip_61051("61051"),
    zip_61052("61052"),
    zip_61053("61053"),
    zip_61054("61054"),
    zip_61057("61057"),
    zip_61061("61061"),
    zip_61064("61064"),
    zip_61068("61068"),
    zip_61071("61071"),
    zip_61074("61074"),
    zip_61078("61078"),
    zip_61081("61081"),
    zip_61084("61084"),
    zip_61091("61091"),
    zip_61230("61230"),
    zip_61243("61243"),
    zip_61250("61250"),
    zip_61251("61251"),
    zip_61252("61252"),
    zip_61261("61261"),
    zip_61270("61270"),
    zip_61277("61277"),
    zip_61283("61283"),
    zip_61285("61285"),
    zip_61310("61310"),
    zip_61318("61318"),
    zip_61324("61324"),
    zip_61331("61331"),
    zip_61353("61353"),
    zip_61367("61367"),
    zip_61378("61378");

    private final String value;

    ZipcodeOption(String value) {
        this.value = value;
    }

    @Override
    public String getLabel() {
        return this.value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getHelpText() {
        return null;
    }

    public static Boolean isValidZipcodeOption(String zipcode) {
        return Arrays.stream(ZipcodeOption.values()).anyMatch(zip -> zip.value.equals(zipcode));
    }
}
