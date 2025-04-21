package com.nashrookie.lavish.dto.response;

import com.nashrookie.lavish.entity.Product;

public record ProductCardDto(Long id, String name, String thumbnailImg, Long price, Double rating) {
    public static ProductCardDto fromModel(Product product) {
        return new ProductCardDto(product.getId(), product.getName(), product.getThumbnailImg(), product.getPrice(),
                product.getRating());
    }
}
