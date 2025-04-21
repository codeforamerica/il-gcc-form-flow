package org.ilgcc.app.utils;

import org.ilgcc.app.email.SendGridValidationResponseBody;
import org.ilgcc.app.email.SendGridValidationResponseBody.*;

public class SendGridValidationResponseBodyBuilder {

  private boolean hasValidAddressSyntax = true;
  private boolean hasMxOrARecord = true;
  private boolean isSuspectedDisposableAddress = false;
  private boolean hasKnownBounces = false;
  private boolean hasSuspectedBounces = false;
  private String suggestion = null;
  private String emailAddress = "";

  public SendGridValidationResponseBodyBuilder withHasValidAddressSyntax(boolean value) {
    this.hasValidAddressSyntax = value;
    return this;
  }

  public SendGridValidationResponseBodyBuilder withHasMxOrARecord(boolean value) {
    this.hasMxOrARecord = value;
    return this;
  }

  public SendGridValidationResponseBodyBuilder withIsSuspectedDisposableAddress(boolean value) {
    this.isSuspectedDisposableAddress = value;
    return this;
  }

  public SendGridValidationResponseBodyBuilder withHasKnownBounces(boolean value) {
    this.hasKnownBounces = value;
    return this;
  }

  public SendGridValidationResponseBodyBuilder withHasSuspectedBounces(boolean value) {
    this.hasSuspectedBounces = value;
    return this;
  }

  public SendGridValidationResponseBodyBuilder withSuggestion(String suggestion) {
    this.suggestion = suggestion;
    return this;
  }

  public SendGridValidationResponseBodyBuilder withEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  public SendGridValidationResponseBodyBuilder withIsSuspectedRoleAddress(boolean isSuspectedRoleAddress) {
    this.isSuspectedDisposableAddress = isSuspectedRoleAddress;
    return this;
  }

  public SendGridValidationResponseBody build() {
    Domain domain = new Domain();
    domain.setHasValidAddressSyntax(hasValidAddressSyntax);
    domain.setHasMxOrARecord(hasMxOrARecord);
    domain.setSuspectedDisposableAddress(isSuspectedDisposableAddress);

    Additional additional = new Additional();
    additional.setHasKnownBounces(hasKnownBounces);
    additional.setHasSuspectedBounces(hasSuspectedBounces);

    LocalPart localPart = new LocalPart(); // Set values if needed
    boolean isSuspectedRoleAddress = false;
    localPart.setSuspectedRoleAddress(isSuspectedRoleAddress);

    Checks checks = new Checks();
    checks.setDomain(domain);
    checks.setAdditional(additional);
    checks.setLocalPart(localPart);

    Result result = new Result();
    result.setEmail(emailAddress);
    result.setHost("example.com");
    result.setVerdict("Valid");
    result.setScore(0.85);
    result.setChecks(checks);
    result.setSuggestion(suggestion);

    SendGridValidationResponseBody body = new SendGridValidationResponseBody();
    body.setResult(result);
    return body;
  }

}
