package com.clever.exception;

public class NotSufficientFundsException extends RuntimeException {

    public NotSufficientFundsException(String message) {
        super(message);
    }
}
