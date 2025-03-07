package org.ilgcc.app.submission.conditions;

import org.springframework.stereotype.Component;

@Component
public class ShowParentHomeAddressConfirmationScreen extends ShowAddressConfirmationScreen {

    public ShowParentHomeAddressConfirmationScreen() {
        super("parentHome");
    }
}
