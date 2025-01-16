package org.ilgcc.app.submission.router;

import lombok.Getter;

@Getter
public class CCRR {
    public final String name;
    public final String providerId;
    public final String SDA;
    public final String caseLoadCode;
//    address??
//    phone number??

    static public CCRR FOUR_C = new CCRR("4C: Community Coordinated Child Care", "56522729391679", "2", "BB");
    static public CCRR PROJECT_CHILD = new CCRR("Project CHILD", "59522729391675", "15", "QQ");
    static public CCRR ILLINOIS_ACTION = new CCRR("Illinois Action for Children", "47522729391670", "6", "GG");

    public CCRR(String name, String providerId, String SDA, String caseLoadCode) {
        this.name = name;
        this.providerId = providerId;
        this.SDA = SDA;
        this.caseLoadCode = caseLoadCode;
    }
}
