package com.nashrookie.lavish.dto.request;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductDto(
                @NotBlank @Length(min = 10, max = 200) String name,
                @Min(0) Long price,
                @NotBlank @Length(min = 10, max = 255) String description,
                @NotNull MultipartFile thumbnailImg,
                @NotNull Boolean isFeatured,
                @Min(0) Integer quantity,
                @NotNull Long categoryId,
                List<MultipartFile> images) {
}
