package com.nashrookie.lavish.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashrookie.lavish.dto.response.CategoryDto;
import com.nashrookie.lavish.repository.CategoryRepository;


import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping()
    public ResponseEntity<List<CategoryDto>> getCategoriesUserView() {
        return ResponseEntity.ok(categoryRepository.findAllBy(CategoryDto.class));
    }
}
