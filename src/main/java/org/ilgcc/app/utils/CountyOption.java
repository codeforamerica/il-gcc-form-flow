package org.ilgcc.app.utils;

public enum CountyOption implements InputOption {

    CARROLL("Carroll"),
    DEKALB("DeKalb"),
    LEE("Lee"),
    MCHENRY("McHenry"),
    OGLE("Ogle"),
    WHITESIDE("Whiteside");

    private final String value;

    CountyOption(String value) {
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
}
