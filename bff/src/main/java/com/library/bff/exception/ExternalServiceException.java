package com.library.bff.exception;

public class ExternalServiceException extends RuntimeException {

    private final int statusCode;

    public ExternalServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}