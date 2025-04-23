package com.nashrookie.lavish.exception;

public class NotFoundProductException extends RuntimeException {
    private static final String MESSAGE = "Can not found product";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
