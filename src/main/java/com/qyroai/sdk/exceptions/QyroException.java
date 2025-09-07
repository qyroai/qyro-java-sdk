package com.qyroai.sdk.exceptions;

public class QyroException extends RuntimeException {
    public QyroException(String message) {
        super(message);
    }

    public QyroException(String message, Throwable cause) {
        super(message, cause);
    }
}