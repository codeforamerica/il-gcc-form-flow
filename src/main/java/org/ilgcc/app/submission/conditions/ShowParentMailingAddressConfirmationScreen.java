package org.ilgcc.app.submission.conditions;

import org.springframework.stereotype.Component;

@Component
public class ShowParentMailingAddressConfirmationScreen extends ShowAddressConfirmationScreen {

    public ShowParentMailingAddressConfirmationScreen() {
        super("parentMailing");
    }
}
