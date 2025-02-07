package org.ilgcc.app.email;

import lombok.Getter;

@Getter
public class EmailConstants {

    @Getter
    public enum EmailType {
        FAMILY_CONFIRMATION_EMAIL("Family Confirmation Email", "familyConfirmationEmailSent"), FAMILY_CONFIRMATION_EMAIL_NO_PROVIDER(
                "No Provider Family Confirmation Email", "familyConfirmationEmailSent");

        private final String description;
        private final String sentVariable;

        EmailType(String description, String sentVariable) {

            this.description = description;
            this.sentVariable = sentVariable;
        }

        public String getDescription() {
            return description;
        }

        public String getSentVariable() { return sentVariable}
    }

    public static final String FROM_ADDRESS = "contact@getchildcareil.org";
}
