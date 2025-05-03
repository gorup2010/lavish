package com.nashrookie.lavish.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashrookie.lavish.dto.filter.CategoryFilterDto;
import com.nashrookie.lavish.dto.filter.ProductFilterDto;
import com.nashrookie.lavish.dto.filter.UserFilterDto;
import com.nashrookie.lavish.dto.response.CategoryInAdminDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.ProductCardInAdminDto;
import com.nashrookie.lavish.dto.response.UserInAdminDto;
import com.nashrookie.lavish.service.CategoryService;
import com.nashrookie.lavish.service.ProductService;
import com.nashrookie.lavish.service.UserService;
import com.nashrookie.lavish.util.PaginationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
//@Secured("ADMIN")
@Slf4j
public class AdminController {

    private final CategoryService categoryService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/categories")
    public ResponseEntity<PaginationResponse<CategoryInAdminDto>> getCategoriesAdminView(
            @Valid @ModelAttribute CategoryFilterDto categoryFilter) {
        Pageable pageable = PaginationUtils.createPageable(categoryFilter.page(),
                categoryFilter.size(),
                categoryFilter.sortBy(),
                categoryFilter.sortOrder());

        return ResponseEntity.ok(categoryService.getCategoriesAdminView(categoryFilter, pageable));
    }

    @GetMapping("/users")
    public ResponseEntity<PaginationResponse<UserInAdminDto>> getUsersAdminView(
            @Valid @ModelAttribute UserFilterDto userFilter) {
        Pageable pageable = PaginationUtils.createPageable(userFilter.page(),
                userFilter.size(),
                userFilter.sortBy(),
                userFilter.sortOrder());

        return ResponseEntity.ok(userService.getUsersAdminView(userFilter, pageable));
    }

    @GetMapping("/products")
    public ResponseEntity<PaginationResponse<ProductCardInAdminDto>> getProductsAdminView(
            @Valid @ModelAttribute ProductFilterDto productFilter) {
        Pageable pageable = PaginationUtils.createPageable(productFilter.page(),
                productFilter.size(),
                productFilter.sortBy(),
                productFilter.sortOrder());

        PaginationResponse<ProductCardInAdminDto> result = productService.getProductsAdminView(productFilter, pageable);
        return ResponseEntity.ok(result);
    }
}
