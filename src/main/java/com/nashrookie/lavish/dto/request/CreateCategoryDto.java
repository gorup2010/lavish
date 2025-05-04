package com.nashrookie.lavish.dto.request;


import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryDto(
        @NotBlank @Length(min = 10, max = 200) String name,
        @NotBlank @Length(min = 10, max = 255) String description,
        @NotNull MultipartFile thumbnailImg) {
}