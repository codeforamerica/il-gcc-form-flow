package org.ilgcc.app.submission.router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.ilgcc.app.utils.ZipcodeOption;

@Getter
public class CCRR {

    protected final String name;
    protected final String providerId;
    protected final String SDA;
    protected final String caseloadCode;
    protected final String slug;


    static public CCRR FOUR_C = new CCRR("4C: Community Coordinated Child Care", "56522729391679", "2", "BB", "4c-ccap-apps");
    static public CCRR PROJECT_CHILD = new CCRR("Project CHILD", "59522729391675", "15", "QQ", "project-child-ccap-apps");
    static public CCRR ILLINOIS_ACTION = new CCRR("Illinois Action for Children", "47522729391670", "6", "GG",
            "illinois-action-for-children-ccap-apps");

    static private final Map<String, CCRR> CCRR_MAP = new HashMap<>();

    static {
        for (CCRR ccrr : CCRR.values()) {
            CCRR_MAP.put(ccrr.caseloadCode, ccrr);
        }
    }

    private static List<CCRR> values() {
        return List.of(FOUR_C, PROJECT_CHILD, ILLINOIS_ACTION);
    }

    public CCRR(String name, String providerId, String SDA, String caseloadCode, String slug) {
        this.name = name;
        this.providerId = providerId;
        this.SDA = SDA;
        this.caseloadCode = caseloadCode;
        this.slug = slug;
    }

    public static Optional<CCRR> findCCRRByCaseLoadCode(String caseloadCode) {
        Boolean caseloadPresent = CCRR_MAP.containsKey(caseloadCode);
        return caseloadPresent ? Optional.of(CCRR_MAP.get(caseloadCode)) : Optional.empty();
    }
}
