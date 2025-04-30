package com.nashrookie.lavish.dto.request;

public record UpdateProductDto(
    String name,
    Long price,
    String description,
    Boolean isFeatured,
    Integer quantity
) {
}
