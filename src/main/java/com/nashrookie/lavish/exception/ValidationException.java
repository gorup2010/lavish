package com.nashrookie.lavish.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ValidationException extends RuntimeException {

    private final MethodArgumentNotValidException ex;

    public ValidationException(MethodArgumentNotValidException ex) {
        this.ex = ex;
    }

    @Override
    public String getMessage() {
        Map<String, List<String>> errors = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.forEach(error -> errors.computeIfAbsent(error.getField(), k -> new java.util.ArrayList<>())
                .add(error.getDefaultMessage()));
        return errors.toString();
    }
}
