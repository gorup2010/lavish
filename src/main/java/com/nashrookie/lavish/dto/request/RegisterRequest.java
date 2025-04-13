package com.nashrookie.lavish.dto.request;

public record RegisterRequest(
    String username,
    String password,
    String firstname,
    String lastname
) {
}