package org.ilgcc.app.utils;

import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.util.UriEncoder;

public class TextUtilities {
  public static String generateTextMessage (String recipient, String textBody){
  Map<String, String> params = new HashMap<>();

  params.put("body", textBody);

  return params.keySet().stream()
      .map(key -> key + "=" + UriEncoder.encode(params.get(key)))
      .collect(joining("&", String.format("sms://%s?", reformatRecipientNumber(recipient)), ""));
  }


  private static String reformatRecipientNumber(String recipient) {
    if (recipient == null) {
      return "";
    }

    String formattedNumber = recipient.replaceAll("\\D", "");
    if (formattedNumber.isEmpty()) {
      return formattedNumber;
    } else {
      return String.format("+1%s", formattedNumber);
    }
  }
}
