package org.ilgcc.app.utils.enums;

import lombok.Getter;

@Getter
public enum ProviderType {

    LICENSED_DAY_CARE_CENTER("centersAndLicensedProvidersType","CENTERS_AND_LICENSED_PROVIDERS_TYPE","LICENSED_DAY_CARE_CENTER_760"),
    LICENSED_DAY_CARE_HOME("centersAndLicensedProvidersType","CENTERS_AND_LICENSED_PROVIDERS_TYPE","LICENSED_DAY_CARE_HOME_762"),
    LICENSED_GROUP_CHILD_CARE_HOME("centersAndLicensedProvidersType","CENTERS_AND_LICENSED_PROVIDERS_TYPE","LICENSED_GROUP_DAY_CARE_HOME_763"),
    LICENSE_EXEMPT_CHILD_CARE_CENTER("centersAndLicensedProvidersType","CENTERS_AND_LICENSED_PROVIDERS_TYPE","DAY_CARE_CENTER_EXEMPT_FROM_LICENSING_761"),
    LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME("careByRelativeType","CARE_BY_RELATIVE_TYPE","CARE_BY_RELATIVE_IN_CHILD_CARE_PROVIDERS_HOME_765"),
    LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME("careByNonRelativeType","CARE_BY_NON_RELATIVE_TYPE","CARE_BY_NON_RELATIVE_IN_CHILD_CARE_PROVIDERS_HOME_764"),
    LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME("careByRelativeType","CARE_BY_RELATIVE_TYPE","CARE_BY_RELATIVE_IN_CHILDS_HOME_767"),
    LICENSE_EXEMPT_NONRELATIVE_IN_CHILDS_HOME("careByNonRelativeType","CARE_BY_NON_RELATIVE_TYPE","CARE_BY_NON_RELATIVE_IN_CHILDS_HOME_766");

    private final String pdfField;
    private final String pdfFieldName;
    private final String pdfMapKey;

    ProviderType(String pdfMapKey, String pdfField, String pdfFieldName) {
        this.pdfMapKey = pdfMapKey;
        this.pdfField = pdfField;
        this.pdfFieldName = pdfFieldName;
    }

    public static String setPdfFieldNameFromName(String name){
        return ProviderType.valueOf(name).pdfFieldName;
    }

    public static String getPdfMapKeyFromName(String name){
        return ProviderType.valueOf(name).pdfMapKey;
    }
}


