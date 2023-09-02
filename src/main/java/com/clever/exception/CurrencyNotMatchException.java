package com.clever.exception;

public class CurrencyNotMatchException extends RuntimeException {

    public CurrencyNotMatchException(String message) {
        super(message);
    }
}
