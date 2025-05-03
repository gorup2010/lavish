package com.nashrookie.lavish.util;

import com.nashrookie.lavish.dto.request.CreateProductDto;
import com.nashrookie.lavish.dto.request.UpdateProductDetailsDto;
import com.nashrookie.lavish.entity.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "thumbnailImg", ignore = true)
    @Mapping(target = "images", ignore = true)
    Product toProduct(CreateProductDto productCreation);

    void updateProduct(@MappingTarget Product product, UpdateProductDetailsDto detailsUpdate);
}