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

        log.info("Signature: " + signature);
        log.info("Timestamp: " + timestamp);

        // Retrieve the raw body of the request
        String requestBody;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }

        log.info("SendGrid Webhook Events request body: {}", requestBody);

        if (!requestBody.endsWith("\r\n")) {
            log.info("SendGrid Webhook Events request body does not end with CRLF");
            requestBody = requestBody + "\r\n";
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

    public static void main(String[] args) {

        try {
            System.out.println("SendGrid webhook demo");

//            String signature = "MEYCIQCF8EmKsKfq9coiq6Bj3J+QTJq38SEzaWjurIlj4jpxRgIhAJXJSjOGO8dPf607bZzJWbAdKVIC5yv+X+fkfApnGceF";
//            String timestamp = "1736983274";
//            String signature = "MEQCIEu6gADNk9helXDRxTLkmRCiC5L1AxDyhZhlePg7BMvNAiBiHKg/bmEH5lHQ/v2CPulGSn36EyQQua3hOW4UuYYpmg==";
//            String timestamp = "1736983346";

//            String signature = "MEUCIQDkFpWmreRbXS4yT+TBWdJImNpsi87eJUyPaydFTHCYVAIgSj1oJayCyOFt3z6N0xPtQILUyw5a1wkZT8No0sr+mAM=";
//            String timestamp = "1736987963";

            String signature = "MEUCIAVJH3CLgcs9JDnBSFWBUlvbBACt2Kq5eGY4ATh6DOmoAiEAhLshMxhNGLrNVDU4KE/KhYzJH82QAI9BDs0tQMzNNXc=";
            String timestamp = "1736988178";

//            String requestBody = "[{\"email\":\"example@test.com\",\"timestamp\":1736983274,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"processed\",\"category\":[\"cat facts\"],\"sg_event_id\":\"3ouYlDUW5zqiVaoeBeYjKg==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\"},{\"email\":\"example@test.com\",\"timestamp\":1736983274,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"deferred\",\"category\":[\"cat facts\"],\"sg_event_id\":\"69ncHWvKpj1Xoke-68p5Tw==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"response\":\"400 try again later\",\"attempt\":\"5\"},{\"email\":\"example@test.com\",\"timestamp\":1736983274,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"delivered\",\"category\":[\"cat facts\"],\"sg_event_id\":\"Qj61xzyHBQQPvRe71zsBqQ==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"response\":\"250 OK\"},{\"email\":\"example@test.com\",\"timestamp\":1736983274,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"open\",\"category\":[\"cat facts\"],\"sg_event_id\":\"FC9wOrH3Qsg6OY4l6Cyt-g==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"useragent\":\"Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)\",\"ip\":\"255.255.255.255\"},{\"email\":\"example@test.com\",\"timestamp\":1736983274,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"click\",\"category\":[\"cat facts\"],\"sg_event_id\":\"2wFPdzLELssk2vzqKPZMVg==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"useragent\":\"Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)\",\"ip\":\"255.255.255.255\",\"url\":\"http://www.sendgrid.com/\"},{\"email\":\"example@test.com\",\"timestamp\":1736983274,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"bounce\",\"category\":[\"cat facts\"],\"sg_event_id\":\"JTROFQq02Sazwa4Dn3H7nw==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"reason\":\"500 unknown recipient\",\"status\":\"5.0.0\"},{\"email\":\"example@test.com\",\"timestamp\":1736983274,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"dropped\",\"category\":[\"cat facts\"],\"sg_event_id\":\"fGyWnizdtLRdOuCTnfleLA==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"reason\":\"Bounced Address\",\"status\":\"5.0.0\"},{\"email\":\"example@test.com\",\"timestamp\":1736983274,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"spamreport\",\"category\":[\"cat facts\"],\"sg_event_id\":\"cmswSuKJlpQtZKGLbZ-98g==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\"},{\"email\":\"example@test.com\",\"timestamp\":1736983274,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"unsubscribe\",\"category\":[\"cat facts\"],\"sg_event_id\":\"y3lE6Jr7WOV5OvFdgVigpw==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\"},{\"email\":\"example@test.com\",\"timestamp\":1736983274,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"group_unsubscribe\",\"category\":[\"cat facts\"],\"sg_event_id\":\"YX9an10LFM_9_z7INf5suA==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"useragent\":\"Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)\",\"ip\":\"255.255.255.255\",\"url\":\"http://www.sendgrid.com/\",\"asm_group_id\":10},{\"email\":\"example@test.com\",\"timestamp\":1736983274,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"group_resubscribe\",\"category\":[\"cat facts\"],\"sg_event_id\":\"yQsOs7FZoDyws2NMgiot6Q==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"useragent\":\"Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)\",\"ip\":\"255.255.255.255\",\"url\":\"http://www.sendgrid.com/\",\"asm_group_id\":10}]";
//            String requestBody = "[{\"email\":\"marcperlman+test@gmail.com\",\"event\":\"open\",\"ip\":\"66.102.7.162\",\"sg_content_type\":\"html\",\"sg_event_id\":\"sOkL0WKYQBuTwBGfHqNmHg\",\"sg_machine_open\":false,\"sg_message_id\":\"SQtFNd5NTCCS7VaSMpHqIg.recvd-5f9ffdf494-zx4m2-1-67882EAE-3.0\",\"timestamp\":1736983345,\"useragent\":\"Mozilla/5.0 (Windows NT 5.1; rv:11.0) Gecko Firefox/11.0 (via ggpht.com GoogleImageProxy)\"}]";
//            String requestBody = "[{\"email\":\"example@test.com\",\"timestamp\":1736987963,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"processed\",\"category\":[\"cat facts\"],\"sg_event_id\":\"Dj2IPRkwl_gKeDPZLhSFOQ==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\"},{\"email\":\"example@test.com\",\"timestamp\":1736987963,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"deferred\",\"category\":[\"cat facts\"],\"sg_event_id\":\"bxpLBCsMfAunt0BIgRejiQ==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"response\":\"400 try again later\",\"attempt\":\"5\"},{\"email\":\"example@test.com\",\"timestamp\":1736987963,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"delivered\",\"category\":[\"cat facts\"],\"sg_event_id\":\"ThVSYztDqBtVt9-vPvz-7Q==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"response\":\"250 OK\"},{\"email\":\"example@test.com\",\"timestamp\":1736987963,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"open\",\"category\":[\"cat facts\"],\"sg_event_id\":\"dSxZTkEpyv5bdFGbpBQTrA==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"useragent\":\"Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)\",\"ip\":\"255.255.255.255\"},{\"email\":\"example@test.com\",\"timestamp\":1736987963,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"click\",\"category\":[\"cat facts\"],\"sg_event_id\":\"1LQQj8fd7tdnrZftzyhENw==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"useragent\":\"Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)\",\"ip\":\"255.255.255.255\",\"url\":\"http://www.sendgrid.com/\"},{\"email\":\"example@test.com\",\"timestamp\":1736987963,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"bounce\",\"category\":[\"cat facts\"],\"sg_event_id\":\"sfLKgaGwXsWgVQZZa5HprA==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"reason\":\"500 unknown recipient\",\"status\":\"5.0.0\"},{\"email\":\"example@test.com\",\"timestamp\":1736987963,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"dropped\",\"category\":[\"cat facts\"],\"sg_event_id\":\"M9s2Cf04m8Bl0FzQoRcPWg==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"reason\":\"Bounced Address\",\"status\":\"5.0.0\"},{\"email\":\"example@test.com\",\"timestamp\":1736987963,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"spamreport\",\"category\":[\"cat facts\"],\"sg_event_id\":\"5iWtP__-1RvbvUwP8VwKlA==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\"},{\"email\":\"example@test.com\",\"timestamp\":1736987963,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"unsubscribe\",\"category\":[\"cat facts\"],\"sg_event_id\":\"xFQbpjrbZujDibsr82H8xw==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\"},{\"email\":\"example@test.com\",\"timestamp\":1736987963,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"group_unsubscribe\",\"category\":[\"cat facts\"],\"sg_event_id\":\"hN-8G3ceH9wH0O9kQpWnPA==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"useragent\":\"Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)\",\"ip\":\"255.255.255.255\",\"url\":\"http://www.sendgrid.com/\",\"asm_group_id\":10},{\"email\":\"example@test.com\",\"timestamp\":1736987963,\"smtp-id\":\"\\u003c14c5d75ce93.dfd.64b469@ismtpd-555\\u003e\",\"event\":\"group_resubscribe\",\"category\":[\"cat facts\"],\"sg_event_id\":\"st3JRcYKmtBCKF7Ca2fACg==\",\"sg_message_id\":\"14c5d75ce93.dfd.64b469.filter0001.16648.5515E0B88.0\",\"useragent\":\"Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)\",\"ip\":\"255.255.255.255\",\"url\":\"http://www.sendgrid.com/\",\"asm_group_id\":10}]";
            String requestBody = "[{\"email\":\"marcperlman+test@gmail.com\",\"event\":\"open\",\"ip\":\"66.102.7.168\",\"sg_content_type\":\"html\",\"sg_event_id\":\"RIrgxZIeRymjP8h0vdl_EA\",\"sg_machine_open\":false,\"sg_message_id\":\"SQtFNd5NTCCS7VaSMpHqIg.recvd-5f9ffdf494-zx4m2-1-67882EAE-3.0\",\"timestamp\":1736988163,\"useragent\":\"Mozilla/5.0 (Windows NT 5.1; rv:11.0) Gecko Firefox/11.0 (via ggpht.com GoogleImageProxy)\"}]";


            if (!requestBody.endsWith("\r\n")) {
                System.out.println("Adding \r\n");
                requestBody = requestBody + "\r\n";
            }


            Security.addProvider(new BouncyCastleProvider());
            final EventWebhook ew = new EventWebhook();
            final ECPublicKey ellipticCurvePublicKey = ew.ConvertPublicKeyToECDSA(
                    "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE8jNzCXjaEPPCUinChpegoT6fhF9u5uNVIAvQZ1ukRVDM4JYd/QfP5/BtU5/YjZO5JsrKU01IhInH/qRe8jyyRw==");

            final boolean valid = ew.VerifySignature(ellipticCurvePublicKey, requestBody, signature, timestamp);

            System.out.println(valid);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
