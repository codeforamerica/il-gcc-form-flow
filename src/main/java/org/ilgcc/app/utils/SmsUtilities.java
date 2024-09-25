package org.ilgcc.app.utils;

import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.util.UriEncoder;

public class SmsUtilities {

    public static String generateWellFormedMessage(String recipient, String emailBody) {
        Map<String, String> params = new HashMap<>();
        params.put("body", emailBody);

        return params.keySet().stream()
                .map(key -> key + "=" + UriEncoder.encode(params.get(key)))
                .collect(joining("&", String.format("sms://%s;?", reformatRecipientNumber(recipient)), ""));
    }

    private static String reformatRecipientNumber(String recipient) {
        String formattedNumber = recipient.replaceAll("\\D", "");
        if (formattedNumber.isEmpty()) {
            return formattedNumber;
        } else {
            return String.format("+1%s", formattedNumber);
        }
    }

    ;
}
