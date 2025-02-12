package org.ilgcc.app.email;

import lombok.Getter;

@Getter
public class EmailConstants {

    @Getter
    public enum EmailType {
        FAMILY_CONFIRMATION_EMAIL("Family Confirmation Email"),
        FAMILY_CONFIRMATION_EMAIL_NO_PROVIDER(
                "No Provider Family Confirmation Email"),
        PROVIDER_CONFIRMATION_EMAIL("Provider Response Confirmation Email");

        private final String description;

        EmailType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

    public static final String FROM_ADDRESS = "contact@getchildcareil.org";
}
