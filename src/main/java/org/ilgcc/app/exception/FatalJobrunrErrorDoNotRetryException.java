package org.ilgcc.app.exception;

import org.jobrunr.JobRunrException;

public class FatalJobrunrErrorDoNotRetryException extends JobRunrException {

    public FatalJobrunrErrorDoNotRetryException(String message) {
        super(message, true);
    }

    public FatalJobrunrErrorDoNotRetryException(String message, Exception e) {
        super(message, true, e);
    }
}
