package com.nashrookie.lavish.dto.request;

public record RegisterRequest(
    String email,
    String password,
    String firstname,
    String lastname
) {
}