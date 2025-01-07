package org.ilgcc.app.email;

import com.sendgrid.helpers.eventwebhook.EventWebhook;
import com.sendgrid.helpers.eventwebhook.EventWebhookHeader;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sendgrid-webhook")
@Slf4j
@Profile({"dev", "staging", "prod", "demo"})
public class SendGridWebhookController {

    @Value("${sendgrid.public-key}")
    String sendGridPublicKey;

//    @PostMapping
//    public void handleSendGridEvents(@RequestBody List<Map<String, Object>> events,
//            @RequestHeader("X-Twilio-Email-Event-Webhook-Signature") String signature,
//            @RequestHeader("X-Twilio-Email-Event-Webhook-Timestamp") String timestamp)
//            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {
//
//        Security.addProvider(new BouncyCastleProvider());
//        final EventWebhook ew = new EventWebhook();
//        final ECPublicKey ellipticCurvePublicKey = ew.ConvertPublicKeyToECDSA(sendGridPublicKey);
//        final boolean valid = ew.VerifySignature(ellipticCurvePublicKey, events.toString().getBytes(), signature, timestamp);
//
//        if (!valid) {
//            log.error("Invalid signature for SendGrid events was provided. Ignoring events.");
//            return;
//        }
//
//        log.info("Received {} SendGrid events", sanitizeEvents(events));
//    }

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
        final boolean valid = ew.VerifySignature(ellipticCurvePublicKey, requestBody, signature, timestamp);

        if (!valid) {
            log.error("Invalid signature for SendGrid events was provided. Ignoring events.");
            return;
        }

        log.info("Received SendGrid events {}", requestBody);
    }

    /**
     * Sanitize the events list by removing new-line characters.
     *
     * @param events: list of events to sanitize
     * @return a sanitized string representation of the events
     */
    private String sanitizeEvents(List<Map<String, Object>> events) {
        return events.toString().replaceAll("[\\r\\n]", "");
    }

//    /**
//     * Convert the public key string to a ECPublicKey.
//     *
//     * @param publicKey: verification key in Mail Settings in SendGrid
//     * @return a public key using the ECDSA algorithm
//     * @throws NoSuchAlgorithmException
//     * @throws NoSuchProviderException
//     * @throws InvalidKeySpecException
//     */
//    private ECPublicKey convertPublicKeyToECDSA(String publicKey)
//            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
//
//        log.info("Converting public key to ECDSA signature");
//        log.info(publicKey);
//
//        Security.addProvider(new BouncyCastleProvider());
//
//        byte[] publicKeyInBytes = Base64.getDecoder().decode(publicKey);
//        KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
//        ECPublicKey ecPublicKey = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(publicKeyInBytes));
//
//        log.info(ecPublicKey.toString());
//        log.info("Done converting public key to ECDSA signature");
//
//        return ecPublicKey;
//    }
//
//    /**
//     * Verify signed event webhook requests.
//     *
//     * @param publicKey: elliptic curve public key
//     * @param payload:   event payload string in the request body
//     * @param signature: value obtained from the 'X-Twilio-Email-Event-Webhook-Signature' header
//     * @param timestamp: value obtained from the 'X-Twilio-Email-Event-Webhook-Timestamp' header
//     * @return true or false if signature is valid
//     * @throws NoSuchAlgorithmException
//     * @throws NoSuchProviderException
//     * @throws InvalidKeyException
//     * @throws SignatureException
//     * @throws IOException
//     */
//    private boolean isValidSignature(ECPublicKey publicKey, String signature, String timestamp, byte[] payload)
//            throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
//
//        log.info("validating signature");
//        log.info(publicKey.toString());
//        log.info(signature);
//        log.info(timestamp);
//        log.info(payload.toString());
//
//        // prepend the payload with the timestamp
//        final ByteArrayOutputStream payloadWithTimestamp = new ByteArrayOutputStream();
//        payloadWithTimestamp.write(timestamp.getBytes());
//        payloadWithTimestamp.write(payload);
//
//        // create the signature object
//        final Signature signatureObject = Signature.getInstance("SHA256withECDSA", "BC");
//        signatureObject.initVerify(publicKey);
//        signatureObject.update(payloadWithTimestamp.toByteArray());
//
//        // decode the signature
//        final byte[] signatureInBytes = Base64.getDecoder().decode(signature);
//
//        // verify the signature
//        return signatureObject.verify(signatureInBytes);
//    }
}
