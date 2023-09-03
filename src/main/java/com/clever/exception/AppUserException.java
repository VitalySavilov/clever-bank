package com.clever.exception;

public class AppUserException extends RuntimeException {

    public AppUserException(String message) {
        super(message);
    }

    public AppUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
