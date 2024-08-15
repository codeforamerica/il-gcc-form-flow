package org.ilgcc.app.exception;

public class S3ObjectNotFoundException extends RuntimeException{
    public S3ObjectNotFoundException(String message) {
        super(message);
    }
}
