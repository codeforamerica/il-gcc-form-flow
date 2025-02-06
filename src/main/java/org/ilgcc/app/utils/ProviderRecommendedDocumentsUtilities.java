package org.ilgcc.app.utils;
import formflow.library.data.Submission;
import java.util.List;
import org.ilgcc.app.utils.enums.ProviderType;

public class ProviderRecommendedDocumentsUtilities {

    public static boolean ssnCardRequired(String providerType, String providerTaxIdType) {
        final List providerTypesRequired = List.of(
                ProviderType.LICENSED_DAY_CARE_HOME.name(),
                ProviderType.LICENSED_GROUP_CHILD_CARE_HOME.name(),
                ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME.name(),
                ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_CHILDS_HOME.name(),
                ProviderType.LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME.name(),
                ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name()
        );
        return providerTypesRequired.contains(providerType) || providerTaxIdType.equals("SSN");
    }

    public static boolean idCardRequired(String providerType) {
        final List providerTypesRequired = List.of(
                ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_PROVIDER_HOME.name(),
                ProviderType.LICENSE_EXEMPT_NONRELATIVE_IN_CHILDS_HOME.name(),
                ProviderType.LICENSE_EXEMPT_RELATIVE_IN_PROVIDER_HOME.name(),
                ProviderType.LICENSE_EXEMPT_RELATIVE_IN_CHILDS_HOME.name()
        );
        return providerTypesRequired.contains(providerType);
    }

    public static boolean childCareLicenseRequired(String providerType) {
        final List providerTypesRequired = List.of(
                ProviderType.LICENSED_DAY_CARE_HOME.name(),
                ProviderType.LICENSED_GROUP_CHILD_CARE_HOME.name(),
                ProviderType.LICENSED_DAY_CARE_CENTER.name()
        );
        return providerTypesRequired.contains(providerType);
    }

    public static boolean einIRSLetter(String providerTaxIdType) {
        return providerTaxIdType.equals("FEIN");
    }

    public static boolean licenseExemptLetterRequired(String providerType) {
        final List providerTypesRequired = List.of(
                ProviderType.LICENSE_EXEMPT_CHILD_CARE_CENTER.name()
        );
        return providerTypesRequired.contains(providerType);
    }

}