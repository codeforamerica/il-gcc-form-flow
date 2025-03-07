package org.ilgcc.app.submission.conditions;

import org.springframework.stereotype.Component;

@Component
public class ShowProviderResponseMailingAddressConfirmationScreen extends ShowAddressConfirmationScreen {

    public ShowProviderResponseMailingAddressConfirmationScreen() {
        super("providerMailing");
    }
}
