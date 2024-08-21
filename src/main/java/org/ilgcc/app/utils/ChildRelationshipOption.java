package org.ilgcc.app.utils;

public enum ChildRelationshipOption implements InputOption {
    CHILD("children-ccap-info.relationship-option.child", "Child"),
    STEP_CHILD("children-ccap-info.relationship-option.stepchild", "Step-child"),
    GRANDCHILD("children-ccap-info.relationship-option.grandchild", "Grandchild"),
    FOSTER_CHILD("children-ccap-info.relationship-option.fosterchild", "Foster-child"),
    NIECE_OR_NEPHEW("children-ccap-info.relationship-option.niecenephew", "Niece/nephew"),
    OTHER("children-ccap-info.relationship-option.other", "Other");

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
