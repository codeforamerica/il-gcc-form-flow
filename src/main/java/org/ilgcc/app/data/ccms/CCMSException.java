package org.ilgcc.app.data.ccms;

import org.springframework.http.HttpStatusCode;

public class CCMSException extends RuntimeException {
    private final HttpStatusCode httpStatusCode;
    private final String responseBody;

    public CCMSException(String message, HttpStatusCode httpStatusCode, String responseBody) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.responseBody = responseBody;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; 
    }
}
