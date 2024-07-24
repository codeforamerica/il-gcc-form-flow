package org.ilgcc.app.utils;

public enum GenderOption implements InputOption {

    MALE("general.inputs.male", "M"),
    FEMALE("general.inputs.female", "F"),
    NONBINARY("general.inputs.non-binary", "NB"),
    TRANSGENDER("general.inputs.transgender", "T");

    private final String label;
    private final String pdfValue;

    GenderOption(String label, String pdfValue) {
        this.label = label;
        this.pdfValue = pdfValue;
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
        return GenderOption.valueOf(name).pdfValue;
    }
}
