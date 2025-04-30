package com.nashrookie.lavish.exception;

public class ResourceNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Can not found resource";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
