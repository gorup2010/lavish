package com.nashrookie.lavish.dto.response;

import com.nashrookie.lavish.entity.ProductImage;

public record ImageDto(Long id, String url, String type) {
    public static ImageDto fromModel(ProductImage image) {
        return new ImageDto(image.getId(), image.getUrl(), image.getType());
    }
}
