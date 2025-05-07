package com.nashrookie.lavish.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProductDetailsDto(
    @NotBlank @Length(min = 1, max = 200) String name,
    @Min(1) Long price,
    @NotBlank @Length(min = 1, max = 255) String description,
    @NotNull Boolean isFeatured,
    @Min(0) Integer quantity,
    @NotNull Long categoryId
) {
}
