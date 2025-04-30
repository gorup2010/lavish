package com.nashrookie.lavish.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashrookie.lavish.dto.filter.CategoryFilterDto;
import com.nashrookie.lavish.dto.response.CategoryDto;
import com.nashrookie.lavish.dto.response.CategoryInAdminDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.repository.CategoryRepository;
import com.nashrookie.lavish.service.CategoryService;
import com.nashrookie.lavish.util.PaginationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<List<CategoryDto>> getCategoriesUserView() {
        return ResponseEntity.ok(categoryRepository.findAllBy(CategoryDto.class));
    }

    @GetMapping("/admin")
    public ResponseEntity<PaginationResponse<CategoryInAdminDto>> getCategoriesAdminView(
            @Valid @ModelAttribute CategoryFilterDto categoryFilter) {
        Pageable pageable = PaginationUtils.createPageable(categoryFilter.page(),
                categoryFilter.size(),
                categoryFilter.sortBy(),
                categoryFilter.sortOrder());

        return ResponseEntity.ok(categoryService.getCategoriesAdminView(categoryFilter, pageable));
    }

}
