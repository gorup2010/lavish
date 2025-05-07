package com.nashrookie.lavish.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashrookie.lavish.dto.request.CreateCategoryDto;
import com.nashrookie.lavish.dto.request.FileImageDto;
import com.nashrookie.lavish.dto.request.UpdateCategoryDetailsDto;
import com.nashrookie.lavish.dto.response.CategoryDetailsDto;
import com.nashrookie.lavish.dto.response.CategoryDto;
import com.nashrookie.lavish.entity.Category;
import com.nashrookie.lavish.exception.ResourceNotFoundException;
import com.nashrookie.lavish.repository.CategoryRepository;
import com.nashrookie.lavish.service.CategoryService;
import com.nashrookie.lavish.util.CategoryMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping()
    public ResponseEntity<List<CategoryDto>> getCategoriesUserView() {
        return ResponseEntity.ok(categoryService.findAll(CategoryDto.class));
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoryDetailsDto> getCategoryDetails(@PathVariable Long id) {
        return ResponseEntity.ok(categoryMapper.toCategoryDetailsDto(categoryService.getCategory(id)));
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Secured("ADMIN")
    public ResponseEntity<String> createCategory(@Valid @ModelAttribute CreateCategoryDto createCategoryDto) {
        categoryService.createCategory(createCategoryDto);
        return ResponseEntity.ok("Create Category Successfully");
    }

    @PatchMapping("/{id}")
    @Secured("ADMIN")
    public ResponseEntity<String> updateCategoryDetails(@PathVariable Long id,
            @Valid @RequestBody UpdateCategoryDetailsDto updateDto) {
        categoryService.updateCategoryDetails(id, updateDto);
        return ResponseEntity.ok("Update category details successfully");
    }

    @PatchMapping("/{id}/thumbnail")
    @Secured("ADMIN")
    public ResponseEntity<String> updateCategoryThumbnail(@PathVariable Long id,
            @ModelAttribute FileImageDto fileImageDto) {
        categoryService.updateCategoryThumbnail(id, fileImageDto);
        return ResponseEntity.ok("Update category thumbnail successfully");
    }

    @DeleteMapping("/{id}")
    @Secured("ADMIN")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Delete Category Successfully");
    }
}
