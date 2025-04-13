package com.nashrookie.lavish.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE = "Username already exists";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
