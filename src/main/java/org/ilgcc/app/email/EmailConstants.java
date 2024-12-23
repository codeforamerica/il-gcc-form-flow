package org.ilgcc.app.email;

import lombok.Getter;

@Getter
public class EmailConstants {
    @Getter
    public enum EmailType {
        FAMILY_CONFIRMATION_EMAIL("Family Confirmation Email"),
        FAMILY_CONFIRMATION_EMAIL_NO_PROVIDER("No Provider Family Confirmation Email");

        private final String description;

        EmailType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public static final String FROM_ADDRESS = "ilccap@codeforamerica.org";
    public static final String SENDER_NAME =
            "Child Care Assistance Program Applications - Code for America on behalf of Illinois Department of Human Services";
}
