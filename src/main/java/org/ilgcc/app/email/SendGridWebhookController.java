package org.ilgcc.app.email;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public void handleSendGridEvents(@RequestBody List<Map<String, Object>> events,
            @RequestHeader("X-Twilio-Email-Event-Webhook-Signature") String signature,
            @RequestHeader("X-Twilio-Email-Event-Webhook-Timestamp") String timestamp)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException 
    {
        if (!isValidSignature(convertPublicKeyToECDSA(sendGridPublicKey), signature, timestamp, events.toString().getBytes())) {
            log.error("Invalid signature for SendGrid events was provided. Ignoring events.");
            return;
        }

        log.info("Received {} SendGrid events", sanitizeEvents(events));
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

    /**
     * Convert the public key string to a ECPublicKey.
     *
     * @param publicKey: verification key in Mail Settings in SendGrid
     * @return a public key using the ECDSA algorithm
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     */
    private ECPublicKey convertPublicKeyToECDSA(String publicKey)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        byte[] publicKeyInBytes = Base64.getDecoder().decode(publicKey);
        KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
        return (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(publicKeyInBytes));
    }

    /**
     * Verify signed event webhook requests.
     *
     * @param publicKey: elliptic curve public key
     * @param payload:   event payload string in the request body
     * @param signature: value obtained from the
     *                   'X-Twilio-Email-Event-Webhook-Signature' header
     * @param timestamp: value obtained from the
     *                   'X-Twilio-Email-Event-Webhook-Timestamp' header
     * @return true or false if signature is valid
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws IOException
     */
    private boolean isValidSignature(ECPublicKey publicKey, String signature, String timestamp, byte[] payload)
            throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        // prepend the payload with the timestamp
        final ByteArrayOutputStream payloadWithTimestamp = new ByteArrayOutputStream();
        payloadWithTimestamp.write(timestamp.getBytes());
        payloadWithTimestamp.write(payload);

        // create the signature object
        final Signature signatureObject = Signature.getInstance("SHA256withECDSA", "BC");
        signatureObject.initVerify(publicKey);
        signatureObject.update(payloadWithTimestamp.toByteArray());

        // decode the signature
        final byte[] signatureInBytes = Base64.getDecoder().decode(signature);

        // verify the signature
        return signatureObject.verify(signatureInBytes);
    }
}
