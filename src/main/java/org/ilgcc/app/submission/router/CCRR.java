package org.ilgcc.app.submission.router;

import lombok.Getter;

@Getter
public class CCRR {

    protected final String name;
    protected final String providerId;
    protected final String SDA;
    protected final String caseloadCode;
    protected final String slug;

    static public CCRR FOUR_C = new CCRR("4C: Community Coordinated Child Care", "56522729391679", "2", "BB", "4c-ccap-apps");
    static public CCRR PROJECT_CHILD = new CCRR("Project CHILD", "59522729391675", "15", "QQ", "project-child-ccap-apps");
    static public CCRR ILLINOIS_ACTION = new CCRR("Illinois Action for Children", "47522729391670", "6", "GG", "illinois-action-for-children-ccap-apps");

    public CCRR(String name, String providerId, String SDA, String caseloadCode, String slug) {
        this.name = name;
        this.providerId = providerId;
        this.SDA = SDA;
        this.caseloadCode = caseloadCode;
        this.slug = slug;
    }
}
