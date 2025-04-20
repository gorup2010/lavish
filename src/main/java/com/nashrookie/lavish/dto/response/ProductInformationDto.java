package com.nashrookie.lavish.dto.response;

import java.util.List;

import com.nashrookie.lavish.entity.Category;
import com.nashrookie.lavish.entity.ProductImage;

public record ProductInformationDto(
        Long id,
        String name,
        Long price,
        Double rating,
        String description,
        String thumbnailImg,
        Boolean isFeatured,
        Integer quantity,
        List<Category> categories,
        List<ProductImage> images) {
}
