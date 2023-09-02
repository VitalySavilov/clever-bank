package com.clever.exception;

public class AccountException extends RuntimeException {

    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountException(String message) {
        super(message);
    }
}
