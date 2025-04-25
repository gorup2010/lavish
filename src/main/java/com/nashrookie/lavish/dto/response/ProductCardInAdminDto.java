package com.nashrookie.lavish.dto.response;

import java.time.ZonedDateTime;

import com.nashrookie.lavish.entity.Product;

public record ProductCardInAdminDto(Long id, String name, String thumbnailImg, Long price, Integer quantity,
        ZonedDateTime createdOn) {
    public static ProductCardInAdminDto fromModel(Product product) {
        return new ProductCardInAdminDto(product.getId(), product.getName(), product.getThumbnailImg(),
                product.getPrice(), product.getQuantity(), product.getCreatedOn());
    }
}
