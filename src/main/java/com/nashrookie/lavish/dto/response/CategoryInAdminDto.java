package com.nashrookie.lavish.dto.response;

import java.time.ZonedDateTime;

import com.nashrookie.lavish.entity.Category;

public record CategoryInAdminDto(
        Long id,
        String name,
        String thumbnailImg,
        String description,
        ZonedDateTime createdOn) {

    public static CategoryInAdminDto fromModel(Category category) {
        return new CategoryInAdminDto(
                category.getId(),
                category.getName(),
                category.getThumbnailImg(),
                category.getDescription(),
                category.getCreatedOn()
        );
    }
}
