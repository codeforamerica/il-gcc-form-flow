package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@TestPropertySource(properties = {"il-gcc.allow-provider-registration-flow=true", "il-gcc.validate-provider-id=false"})

public class ProviderresponseResgistrationFamilyResponseIntro extends AbstractBasePageTest {

    private static String CONF_CODE = "A2123B";

    @Test
    void testProviderresponseResgistrationFamilyResponseIntro() {
        testPage.navigateToFlowScreen("providerresponse/registration-family-response-intro");

        // registration-family-response-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("registration-family-response-intro.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("registration-family-response-intro.title"));
        assertThat(testPage.findElementById("registration-family-response-intro.box.header").getText()).isEqualTo(getEnMessage("registration-family-response-intro.box.header"));
        assertThat(testPage.findElementById("registration-family-response-intro.1").getText()).isEqualTo(getEnMessage("registration-family-response-intro.1"));
        assertThat(testPage.findElementById("registration-family-response-intro.2").getText()).isEqualTo(getEnMessage("registration-family-response-intro.2"));

        testPage.clickContinue();
    }

}
