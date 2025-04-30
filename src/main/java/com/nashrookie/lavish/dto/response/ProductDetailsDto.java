package com.nashrookie.lavish.dto.response;

import java.util.List;

import com.nashrookie.lavish.entity.Product;

public record ProductDetailsDto(
                Long id,
                String name,
                Long price,
                Double rating,
                String description,
                String thumbnailImg,
                Boolean isFeatured,
                Integer quantity,
                List<CategoryDto> categories,
                List<ImageDto> images) {

        public static ProductDetailsDto fromModel(Product product) {
                List<CategoryDto> categories = product.getCategories().stream().map(CategoryDto::fromModel).toList();
                List<ImageDto> images = product.getImages().stream().map(ImageDto::fromModel).toList();
                return new ProductDetailsDto(product.getId(), product.getName(), product.getPrice(),
                                product.getRating(), product.getDescription(), product.getThumbnailImg(),
                                product.getIsFeatured(), product.getQuantity(), categories, images);
        }
}
