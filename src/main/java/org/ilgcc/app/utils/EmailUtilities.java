package org.ilgcc.app.utils;

import static java.util.stream.Collectors.joining;

import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.util.UriEncoder;

public class EmailUtilities {

    public static String generateWellFormedEmail(String recipient, String subject, String emailBody) {
        Map<String, String> params = new HashMap<>();
        params.put("subject", subject);
        params.put("body", emailBody);

        return params.keySet().stream().map(key -> key + "=" + UriEncoder.encode(params.get(key)))
                .collect(joining("&", String.format("mailto:%s?", recipient), ""));
    }
}
