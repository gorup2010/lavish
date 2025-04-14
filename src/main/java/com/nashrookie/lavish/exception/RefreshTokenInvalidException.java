package com.nashrookie.lavish.exception;

public class RefreshTokenInvalidException extends RuntimeException {
    private static final String MESSAGE = "Invalid";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}