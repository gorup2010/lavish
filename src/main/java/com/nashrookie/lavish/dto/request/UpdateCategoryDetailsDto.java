package com.nashrookie.lavish.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryDetailsDto(
        @NotBlank @Length(min = 1, max = 200) String name,
        @NotBlank @Length(min = 1, max = 255) String description) {
}
