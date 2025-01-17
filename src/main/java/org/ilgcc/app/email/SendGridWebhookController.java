package org.ilgcc.app.email;

import com.sendgrid.helpers.eventwebhook.EventWebhook;
import com.sendgrid.helpers.eventwebhook.EventWebhookHeader;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sendgrid-webhook")
@Slf4j
@Profile({"dev", "staging", "prod", "demo"})
public class SendGridWebhookController {

    @Value("${sendgrid.public-key}")
    String sendGridPublicKey;

    @PostMapping
    public void handleSendGridEvents(HttpServletRequest request)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {

        String signature = request.getHeader(EventWebhookHeader.SIGNATURE.toString());
        String timestamp = request.getHeader(EventWebhookHeader.TIMESTAMP.toString());

        // Retrieve the raw body of the request
        String requestBody;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }

        Security.addProvider(new BouncyCastleProvider());
        final EventWebhook ew = new EventWebhook();
        final ECPublicKey ellipticCurvePublicKey = ew.ConvertPublicKeyToECDSA(sendGridPublicKey);

        // Sendgrid payloads should always end with a CRLF for validation
        boolean valid = ew.VerifySignature(ellipticCurvePublicKey, requestBody  + "\r\n", signature, timestamp);

        if (!valid) {
            // Sendgrid's test API doesn't apparently have a trailing CRLF, so we can try again
            // But also raise this error here, and have an alert so if this ever happens in reality
            // we can debug further
            log.warn("Invalid signature for SendGrid events was provided. Removing CRLF modifier.");
            valid = ew.VerifySignature(ellipticCurvePublicKey, requestBody, signature, timestamp);

            if (!valid) {
                log.error("Invalid signature for SendGrid events was provided. Ignoring events. Payload: {} Signature: {}, Timestamp: {}", requestBody, signature, timestamp);
                return;
            }
        }

        log.info("Received SendGrid events {}", requestBody);
    }
}
