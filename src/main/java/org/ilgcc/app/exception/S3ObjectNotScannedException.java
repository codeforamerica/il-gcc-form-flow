package org.ilgcc.app.exception;

public class S3ObjectNotScannedException extends RuntimeException{
    public S3ObjectNotScannedException(String message) {
        super(message);
    }
}
