package org.ilgcc.app.utils;

import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.util.UriEncoder;

public class TextUtilities {

  public static String generateTextMessage(String recipient, String textBody) {
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

  /**
   * Removes all characters that are not A-Z, a-z, 0-9, hyphens, and underscores and replaces with an empty string.
   *
   * @param string the string to be sanitized
   * @return the sanitized string
   */
  public static String sanitize(String string) {
    return sanitize(string, "");
  }

  /**
   * Removes all characters that are not A-Z, a-z, 0-9, hyphens, and underscores
   *
   * @param string the string to be sanitized
   * @param replacementValue the value to insert if an invalid character is found
   * @return the sanitized string
   */
  public static String sanitize(String string, String replacementValue) {
    if (string == null) {
      return null;
    }

    return string
            .replaceAll("[^a-zA-Z0-9_-]", replacementValue)
            .replaceAll("\n", replacementValue)
            .replaceAll("\r", replacementValue);
  }
}
