package org.ilgcc.app.inputs;

public class InputsConstants {
    public static final String FEIN_REGEX = "\\d{2}-\\d{7}";
    public static final String ZIPCODE_REGEX = "^$|^\\d{5}(?:-\\d{4})?$";
    public static final String ITIN_REGEX = "^9\\d{2}-\\d{2}-\\d{4}$";
    public static final String PHONE_REGEX = "\\([2-9][0-8][0-9]\\)\\s\\d{3}-\\d{4}";
}
