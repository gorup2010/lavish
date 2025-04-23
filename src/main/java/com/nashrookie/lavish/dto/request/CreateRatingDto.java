package com.nashrookie.lavish.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CreateRatingDto(
    Long productId, 
    @Min(1) @Max(5) Integer star, 
    @Length(max = 255) String comment) {
}
