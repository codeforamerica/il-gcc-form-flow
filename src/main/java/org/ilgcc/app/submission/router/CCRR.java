package org.ilgcc.app.submission.router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
public class CCRR {

    protected final String name;
    protected final String organizationId;
    protected final String SDA;
    protected final String caseloadCode;
    protected final String slug;
    protected final String phoneNumber;


    public static final CCRR FOUR_C = new CCRR("4C: Community Coordinated Child Care", "56522729391679", "2", "BB",
            "4c-ccap-apps", "(815) 758-8149 x225");
    public static final CCRR PROJECT_CHILD = new CCRR("Project CHILD", "59522729391675", "15", "QQ", "project-child-ccap-apps", "(618) 244-2210");
    public static final CCRR ILLINOIS_ACTION = new CCRR("Illinois Action for Children", "47522729391670", "6", "GG",
            "illinois-action-for-children-ccap-apps", "(312) 823-1100");

    private static final Map<String, CCRR> CCRR_MAP_BY_CASELOAD = new HashMap<>();
    private static final Map<String, CCRR> CCRR_MAP_BY_ORGANIZATIONAL_ID = new HashMap<>();

    static {
        for (CCRR ccrr : CCRR.values()) {
            CCRR_MAP_BY_CASELOAD.put(ccrr.caseloadCode, ccrr);
        }
    }

    static {
        for (CCRR ccrr : CCRR.values()) {
            CCRR_MAP_BY_ORGANIZATIONAL_ID.put(ccrr.organizationId, ccrr);
        }
    }

    public CCRR(String name, String organizationId, String SDA, String caseloadCode, String slug, String phoneNumber) {
        this.name = name;
        this.organizationId = organizationId;
        this.SDA = SDA;
        this.caseloadCode = caseloadCode;
        this.slug = slug;
        this.phoneNumber = phoneNumber;
    }

    private static List<CCRR> values() {
        return List.of(FOUR_C, PROJECT_CHILD, ILLINOIS_ACTION);
    }

    private static Optional<CCRR> findCCRRByCaseLoadCode(String caseloadCode) {
        final boolean caseloadPresent = CCRR_MAP_BY_CASELOAD.containsKey(caseloadCode);
        return caseloadPresent ? Optional.of(CCRR_MAP_BY_CASELOAD.get(caseloadCode)) : Optional.empty();
    }

    private static Optional<CCRR> findCCRRByOrganizationalId(String organizationId) {
        final boolean orgIdPresent = CCRR_MAP_BY_ORGANIZATIONAL_ID.containsKey(organizationId);
        return orgIdPresent ? Optional.of(CCRR_MAP_BY_ORGANIZATIONAL_ID.get(organizationId)) : Optional.empty();
    }

    public static Optional<String> findOrgIdByCaseLoadCode(String caseloadCode) {
        final Optional<CCRR> ccrr = findCCRRByCaseLoadCode(caseloadCode);
        return ccrr.isPresent() ? Optional.of(ccrr.get().organizationId) : Optional.empty();
    }

    public static String findCCRRNameByOrganizationalId(String organizationId) {
        final Optional<CCRR> ccrr = findCCRRByOrganizationalId(organizationId);
        return ccrr.isPresent() ? ccrr.get().name : "";
    }

    public static String getCCRRSlugByOrganizationId(String organizationId) {
        final Optional<CCRR> ccrr = findCCRRByOrganizationalId(organizationId);
        return ccrr.isPresent() ? ccrr.get().slug : "";
    }

    public static String findCCRRPhoneNumberByOrganizationId(String organizationId) {
        final Optional<CCRR> ccrr = findCCRRByOrganizationalId(organizationId);
        return ccrr.isPresent() ? ccrr.get().phoneNumber : "";
    }
}
