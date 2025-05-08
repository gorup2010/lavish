package com.nashrookie.lavish.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @Email String email,
    @NotBlank @Length(min = 8) String password,
    @NotBlank @Length(min = 1, max = 100) String firstname,
    @NotBlank @Length(min = 1, max = 100) String lastname
) {
}