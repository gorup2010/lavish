package com.nashrookie.lavish.dto.response;

import com.nashrookie.lavish.entity.Category;

public record CategoryDto(Long id, String name, String description, String thumbnailImg) {
    public static CategoryDto fromModel(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.getDescription(), category.getThumbnailImg());
    }
}
