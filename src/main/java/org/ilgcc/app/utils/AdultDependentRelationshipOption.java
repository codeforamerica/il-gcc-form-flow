package org.ilgcc.app.utils;

public enum AdultDependentRelationshipOption implements InputOption {
    AUNT("general.relationship-option.aunt-uncle", "Aunt or Uncle"),
    CHILD("general.relationship-option.child", "Child"),
    PARENT("general.relationship-option.parent", "Parent"),
    STEP_PARENT("general.relationship-option.step-parent", "Step Parent"),
    STEP_CHILD("general.relationship-option.step-child", "Step-child"),
    SIBLING("general.relationship-option.sibling", "Sibling"),
    SPOUSE("general.relationship-option.spouse", "Spouse or Partner"),
    COUSIN("general.relationship-option.cousin", "Cousin"),
    GRANDPARENT("general.relationship-option.grandparent", "Grandparent"),
    OTHER("general.relationship-option.other", "Other");

    private final String label;
    public final String pdfValue;

    AdultDependentRelationshipOption(String label, String pdfValue) {
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
}
