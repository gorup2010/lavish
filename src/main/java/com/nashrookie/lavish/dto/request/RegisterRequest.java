package com.nashrookie.lavish.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Email String email,
    @Size(min = 8) String password,
    @Size(min = 100) String firstname,
    @Size(min = 100) String lastname
) {
}