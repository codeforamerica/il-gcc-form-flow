package org.ilgcc.app.email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SendGridValidationResponseBody {

  private Result result;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Result {

    private String email;
    private String verdict;
    private double score;
    private String localPart;
    private String host;
    private String ip_address;
    private Checks checks;
    private String suggestion;

    public boolean hasValidAddressSyntax() {
      return checks != null && checks.domain != null && checks.domain.hasValidAddressSyntax;
    }

    public boolean hasMxOrARecord() {
      return checks != null && checks.domain != null && checks.domain.hasMxOrARecord;
    }

    public boolean isSuspectedDisposableAddress() {
      return checks != null && checks.domain != null && checks.domain.isSuspectedDisposableAddress;
    }

    public boolean hasKnownBounces() {
      return checks != null && checks.additional != null && checks.additional.hasKnownBounces;
    }

    public boolean hasSuspectedBounces() {
      return checks != null && checks.additional != null && checks.additional.hasSuspectedBounces;
    }

    public Boolean hasSuggestedEmailAddress() {
      return suggestion != null && !suggestion.isBlank();
    }

    public String getSuggestedEmailAddress() {
      return suggestion;
    }

    public Boolean isSuspectedRoleAddress() {
      return checks != null && checks.localPart != null && checks.localPart.isSuspectedRoleAddress;
    }
  }

  @Data
  public static class Checks {

    private Domain domain;
    @JsonProperty("local_part")
    private LocalPart localPart;

    private Additional additional;
  }

  @Data
  public static class Domain {

    @JsonProperty("has_valid_address_syntax")
    private boolean hasValidAddressSyntax;

    @JsonProperty("has_mx_or_a_record")
    private boolean hasMxOrARecord;

    @JsonProperty("is_suspected_disposable_address")
    private boolean isSuspectedDisposableAddress;
  }

  @Data
  public static class LocalPart {

    @JsonProperty("is_suspected_role_address")
    private boolean isSuspectedRoleAddress;
  }

  @Data
  public static class Additional {

    @JsonProperty("has_known_bounces")
    private boolean hasKnownBounces;

    @JsonProperty("has_suspected_bounces")
    private boolean hasSuspectedBounces;
  }

}
