package com.nashrookie.lavish.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record CreateProductDto(String name, Long price, String description, String thumbnailImg, Boolean isFeatured,
        Integer quantity,
        List<Long> categorieIds, List<MultipartFile> images) {
}
