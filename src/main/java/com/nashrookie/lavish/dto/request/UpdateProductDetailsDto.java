package com.nashrookie.lavish.dto.request;

public record UpdateProductDetailsDto(
    String name,
    Long price,
    String description,
    Boolean isFeatured,
    Integer quantity,
    Long categoryId
) {
}
