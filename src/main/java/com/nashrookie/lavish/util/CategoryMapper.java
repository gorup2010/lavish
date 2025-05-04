package com.nashrookie.lavish.util;

import com.nashrookie.lavish.dto.request.CreateCategoryDto;
import com.nashrookie.lavish.dto.request.UpdateCategoryDetailsDto;
import com.nashrookie.lavish.dto.response.CategoryDetailsDto;
import com.nashrookie.lavish.entity.Category;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "thumbnailImg", ignore = true)
    Category toCategory(CreateCategoryDto productCreation);

    CategoryDetailsDto toCategoryDetailsDto(Category category);

    void updateCategoryDetails(@MappingTarget Category category, UpdateCategoryDetailsDto detailsUpdate);
}