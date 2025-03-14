package org.ilgcc.app.utils;

public enum ChildRelationshipOption implements InputOption {
    CHILD("general.relationship-option.child", "Child"),
    STEP_CHILD("general.relationship-option.step-child", "Step-child"),
    GRANDCHILD("general.relationship-option.grandchild", "Grandchild"),
    FOSTER_CHILD("general.relationship-option.foster-child", "Foster-child"),
    NIECE_OR_NEPHEW("general.relationship-option.niece-nephew", "Niece/nephew"),
    OTHER("general.relationship-option.other", "Other");

    private final String label;
    public final String pdfValue;

    ChildRelationshipOption(String label, String pdfValue) {
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
